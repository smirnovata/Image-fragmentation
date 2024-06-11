import os
from threading import Thread

import requests
from flask import Flask
from flask import request
import traceback

from processing import Processing, ImageInfo

app = Flask(__name__)
HO_SERVER = os.environ.get('HO_SERVER', "http://localhost:8080")
PYSER_DEBUG = os.environ.get('PYSER_DEBUG', True)

@app.route("/processing/<id>/start", methods=['POST'])
def processing_start(id):
    t = Thread(target=start, args=(id, request.get_json(),))
    t.start()
    return "Starting", 202

def start(id, form):
    print(id)
    print(form)
    try:
        Processing().start(id, form)
        update_status(id, "FINISHED")
    except Exception  :
        print(traceback.format_exc())
        update_status(id, "FAILED")

@app.route('/images/info', methods=['GET'])
def get_img_info():
    try:
        print(request.args.get('path'))
        return ImageInfo().get_image_info(request.args.get('path'))
    except Exception:
        print(traceback.format_exc())
        return "Error", 500

def update_status(id, status):
    print("sent status")
    return requests.post(HO_SERVER + '/api/status/{}/update'.format(id), data='{"status": "'+status+'"}', headers={'content-type': 'application/json'})

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=PYSER_DEBUG)


