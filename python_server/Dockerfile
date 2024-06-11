# syntax=docker/dockerfile:1.4
FROM pytorch/pytorch:2.3.1-cuda11.8-cudnn8-runtime AS builder

WORKDIR py_ser/

COPY requirements.txt .

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        ca-certificates \
        curl \
        git \
        libopenblas-dev \
        libopenmpi-dev \
        python3-pip \
        software-properties-common \
        wget \
    && rm -rf /var/lib/apt/lists/*

# RUN --mount=type=cache,target=/root/.cache/pip \
  # pip3 install flask
RUN pip3 install -r requirements.txt
RUN pip3 install ultralytics opencv-python-headless

COPY /app .

ENTRYPOINT ["python3"]
CMD ["server.py"]

# FROM builder as dev-envs
#
# RUN <<EOF
# apk update
# apk add git
# EOF
#
# RUN <<EOF
# addgroup -S docker
# adduser -S --shell /bin/bash --ingroup docker vscode
# EOF
# # install Docker tools (cli, buildx, compose)
# COPY --from=gloursdocker/docker / /