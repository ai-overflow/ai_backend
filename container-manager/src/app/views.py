import os
import subprocess
from os.path import isfile, join
from os import listdir

from app import app
from flask import request
import json


@app.route('/api/v1/container', methods=["GET"])
def get_containers():
    data = request.data
    subprocess.run(["docker-compose", "up", "-d"])
    return 'run...'


@app.route('/api/v1/container', methods=["POST"])
def start_container():
    data = json.loads(request.get_data())

    folder = "/projects/" + data['project_folder']
    if os.path.isdir(folder):

        # TODO: Move to own Thread
        subprocess.run(["docker-compose", "up", "-d"], cwd=folder)
        return json.dumps({"success": True})
    return json.dumps({"error": "Can't find Project"})


if __name__ == '__main__':
    app.run()