import json
import os
import pathlib
from os import listdir
from os.path import isfile, join

import cv2
import imageio
import numpy as np
from PIL import Image
from torchvision.io.image import read_image
from torchvision.models import segmentation
from torchvision.transforms.functional import to_pil_image

FILES_FOLDER = os.environ.get('FILES_FOLDER', 'D:/Study/4 sem/_test v2/image_fragmentation/tmp')

model_test = {
    "FCN_ResNet50, DEFAULT": (segmentation.fcn_resnet50(weights=segmentation.FCN_ResNet50_Weights.DEFAULT),
                              segmentation.FCN_ResNet50_Weights.DEFAULT),
    "FCN_ResNet101, DEFAULT": (segmentation.fcn_resnet101(weights=segmentation.FCN_ResNet101_Weights.DEFAULT),
                               segmentation.FCN_ResNet101_Weights.DEFAULT),
    "LRASPP_MobileNet, DEFAULT": (
        segmentation.lraspp_mobilenet_v3_large(weights=segmentation.LRASPP_MobileNet_V3_Large_Weights.DEFAULT),
        segmentation.LRASPP_MobileNet_V3_Large_Weights.DEFAULT),
    "DeepLabV3_ResNet101, DEFAULT": (
        segmentation.deeplabv3_resnet101(weights=segmentation.DeepLabV3_ResNet101_Weights.DEFAULT),
        segmentation.LRASPP_MobileNet_V3_Large_Weights.DEFAULT),
    "DeepLabV3_MobileNet50, DEFAULT": (
        segmentation.deeplabv3_resnet50(weights=segmentation.DeepLabV3_ResNet50_Weights.DEFAULT),
        segmentation.DeepLabV3_ResNet50_Weights.DEFAULT),
    "DeepLabV3_ResNet50, DEFAULT": (segmentation.deeplabv3_mobilenet_v3_large(
        weights=segmentation.DeepLabV3_MobileNet_V3_Large_Weights.DEFAULT),
                                    segmentation.DeepLabV3_MobileNet_V3_Large_Weights.DEFAULT)}
for model_name, (model, weights) in model_test.items():
    model.eval()


class Processing:

    def save_imgs(self, imgs, img_folder):
        for img_name, img in imgs.items():
            path = img_folder + '/' + img_name + '.png'
            img.save(path)
            (h, w) = img.size
            if 'image' in img_name:
                save_info(self.get_colors(img), (h, w), path)

    def get_masks(self, img_folder, img_name):
        result = {}

        img = read_image(img_folder + '/' + img_name)
        for model_name, (model, weights) in model_test.items():
            preprocess = weights.transforms()
            batch = preprocess(img).unsqueeze(0)
            prediction = model(batch)["out"]
            normalized_masks = prediction.softmax(dim=1)
            class_to_idx = {cls: idx for (idx, cls) in enumerate(weights.meta["categories"])}
            mask = normalized_masks[0, class_to_idx["__background__"]]
            result[model_name + '_-_' + 'mask'] = to_pil_image(mask)
        return result

    def less_color(self, color_count, img):
        for i in range(1, 256):
            div = i
            quantized = img // div * div + div // 2
            q1 = Image.fromarray(quantized)
            count = len(q1.getcolors(q1.size[0] * q1.size[1]))
            # print(count)
            if (count < color_count):
                return quantized

    def start(self, id, form):
        folder = FILES_FOLDER
        processing_folder = folder + '/' + id + '/archive'
        processing_result_folder = processing_folder + '/result'
        print(processing_folder)
        print(processing_result_folder)
        result = {}
        print("start ", id)

        img_name = [f for f in listdir(processing_folder) if isfile(join(processing_folder, f)) and 'json' not in f][0]
        path = processing_folder + '/' + img_name
        print(path)
        if 'gif' in path:
            img = imageio.mimread(path)[0]
        else:
            img = cv2.imread(path)[:, :, ::-1]

        if form['trySeg']:
            result_mask = self.get_masks(processing_folder, img_name)

            start_id = self.add_kmeans_result(1, form, result, result_mask, img)
            self.add_div_result(start_id + 1, form, result, result_mask, img)
        else:
            main = self.less_color(int(form['numbers']), img)
            result['1_' + 'image'] =resize_image(Image.fromarray(main),  form['height'])

            main2 = self.kmeans_color_quantization(img, form['numbers'])
            result['2_' + 'image'] = resize_image(Image.fromarray(main2),  form['height'])
        self.save_imgs(result, processing_result_folder)

    def add_div_result(self, start_id, form, result, result_mask, test_img):
        main = self.less_color(int(form['numbers'] * (100 - form['backgraundColor']) / 100), test_img)
        background = self.less_color(int(form['numbers'] * form['backgraundColor'] / 100), test_img)
        for id, (name, mask) in enumerate(result_mask.items()):
            resize_res = mask.resize((test_img.shape[1], test_img.shape[0]), Image.LANCZOS)
            resize_res_mask = np.asarray(resize_res)
            mask_height, mask_width, _ = np.asarray(main).shape
            example_result = np.zeros(shape=(mask_height, mask_width, 3)).astype(np.uint8)
            for i in range(0, mask_height):
                for j in range(0, mask_width):
                    # print(resize_res_mask[i][j])
                    if resize_res_mask[i][j] <= 100:
                        example_result[i][j] = main[i][j]
                    else:
                        example_result[i][j] = background[i][j]
            result[str(start_id + id) + '_' + 'mask'] = to_pil_image(resize_res_mask)
            result[str(start_id + id) + '_' + 'image'] = to_pil_image(example_result)
        return len(result_mask.items()) + start_id

    def add_kmeans_result(self, start_id, form, result, result_mask, test_img):
        main = self.kmeans_color_quantization(test_img, int(form['numbers'] * (100 - form['backgraundColor']) / 100))
        background = self.kmeans_color_quantization(test_img, int(form['numbers'] * form['backgraundColor'] / 100))
        for id, (name, mask) in enumerate(result_mask.items()):
            resize_res = mask.resize((test_img.shape[1], test_img.shape[0]), Image.LANCZOS)
            resize_res_mask = np.asarray(resize_res)
            mask_height, mask_width, _ = np.asarray(main).shape
            example_result = np.zeros(shape=(mask_height, mask_width, 3)).astype(np.uint8)
            for i in range(0, mask_height):
                for j in range(0, mask_width):
                    # print(resize_res_mask[i][j])
                    if resize_res_mask[i][j] <= 100:
                        example_result[i][j] = main[i][j]
                    else:
                        example_result[i][j] = background[i][j]
            result[str(id + start_id) + '_' + 'mask'] = to_pil_image(resize_res_mask)
            result[str(id + start_id) + '_' + 'image'] = to_pil_image(example_result)
        return len(result_mask.items()) + start_id


    @staticmethod
    def kmeans_color_quantization(image, clusters=8, rounds=1):
        h, w = image.shape[:2]
        samples = np.zeros([h * w, 3], dtype=np.float32)
        count = 0

        for x in range(h):
            for y in range(w):
                samples[count] = image[x][y]
                count += 1

        _, labels, centers = cv2.kmeans(samples,
                                                  clusters,
                                                  None,
                                                  (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 10000, 0.0001),
                                                  rounds,
                                                  cv2.KMEANS_RANDOM_CENTERS)

        centers = np.uint8(centers)
        res = centers[labels.flatten()]
        return res.reshape(image.shape)

    @staticmethod
    def get_colors(img_arr):
        colors = img_arr.getcolors(img_arr.size[0] * img_arr.size[1])
        # colors.sort()
        return colors


class ImageInfo:
    def get_image_info(self, path):
        if 'gif' in path:
            img = imageio.mimread(path)[0]
        else:
            img = cv2.imread(path)[:, :, ::-1]

        _all = self.get_colors(img)
        save_info(_all, (img.shape[0], img.shape[1]), path)
        return {
            "height": img.shape[0],
            "weight": img.shape[1],
            "colorQuantity": len(_all),
            "colorAndCount": {}
        }

    @staticmethod
    def get_colors(img_arr):
        pil_img = Image.fromarray(img_arr)
        colors = pil_img.getcolors(pil_img.size[0] * pil_img.size[1])
        return colors


def save_info(all, sizes, path):
    result = {}
    for count, color in all:
        result['#%02x%02x%02x' % (color[0], color[1], color[2])] = count
    with open(str(pathlib.Path(path).parent) + '/' + str(pathlib.Path(path).stem) + '.json', 'w') as f:
        json.dump({
            "height": sizes[0],
            "weight": sizes[1],
            "colorQuantity": len(all),
            "colorAndCount": result
        }, f)


def resize_image(pil_image, new_height: int):
    with pil_image as img:
        original_width, original_height = img.size
        new_width = int(original_width * (new_height / original_height))
        resized_img = img.resize((new_width, new_height))
        return resized_img
