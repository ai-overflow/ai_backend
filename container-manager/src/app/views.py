import json
import os
import subprocess
import docker

from app import app
from flask import request


@app.route('/api/v1/container', methods=["GET"])
def get_containers():
    client = docker.from_env()
    value = list(map(lambda x: {"name": x.name, "status": x.status}, client.containers.list()))
    print(value)
    return json.dumps(value)


@app.route('/api/v1/container', methods=["POST"])
def start_container():
    return docker_compose_command(["docker-compose", "up", "-d"])


@app.route('/api/v1/container', methods=["DELETE"])
def stop_container():
    return docker_compose_command(["docker-compose", "down"])


def docker_compose_command(command):
    data = json.loads(request.get_data())
    folder = "/projects/" + data['project_folder']
    if os.path.isdir(folder):
        # TODO: Move to own Thread
        status_code = subprocess.run(command, cwd=folder)
        if status_code.returncode == 0:
            return json.dumps({"success": True})
        return json.dumps({"success": False, "message": "Failed with status code: " + str(status_code.returncode)})
    return json.dumps({"success": False, "message": "Can't find Project"})


if __name__ == '__main__':
    app.run()
