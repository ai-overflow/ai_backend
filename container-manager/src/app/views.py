import json
import os
import subprocess

from app import app
from flask import request


@app.route('/api/v1/container', methods=["GET"])
def get_containers():
    data = request.data
    subprocess.run(["docker-compose", "up", "-d"])
    return 'run...'


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
