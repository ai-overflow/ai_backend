import json
import os
import subprocess
import time
from concurrent import futures

import docker
from app import app
from flask import request
from flask_caching import Cache

cache = Cache(config={'CACHE_TYPE': 'SimpleCache'})
cache.init_app(app)

@app.route('/api/v1/container', methods=["GET"])
def get_containers():
    client = docker.from_env()
    value = list(map(lambda x: {"name": x.name, "status": x.status}, client.containers.list()))
    print(value)
    return json.dumps(value)


@app.route('/api/v1/container/stats/', defaults={"name": ""})
@app.route('/api/v1/container/stats/<string:name>', methods=["GET"])
@cache.cached(timeout=50)
def get_container_stats(name):
    client = docker.from_env()

    selected_container = list(filter(lambda c: c.name.startswith(name), client.containers.list()))

    # maximum 10 threads, but at least as many threads as containers
    with futures.ThreadPoolExecutor(min(10, len(selected_container))) as executor:
        jobs = []
        results_done = []

        for container in selected_container:
            jobs.append(executor.submit(lambda c: c.stats(stream=False), container))

        for job in futures.as_completed(jobs):
            results_done.append(job.result())

        selected_container = results_done

    selected_container = list(map(lambda c: {
        'id': c['id'],
        'name': (c['name'][1:] if c['name'].startswith("/") else c['name']),
        'memory': {
            'total': c['memory_stats']['limit'],
            'used': c['memory_stats']['usage'],
            'used_percentage': round((c['memory_stats']['usage'] / c['memory_stats']['limit']) * 100, 2)
        }
    }, selected_container))

    return json.dumps(selected_container)


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
        output = subprocess.run(command, cwd=folder, capture_output=True, text=True)
        if output.returncode == 0:
            return json.dumps({"success": True, "output": output.stdout})
        return json.dumps({"success": False, "message": "Failed with status code: " + str(output.returncode),
                           "output": output.stderr})
    return json.dumps({"success": False, "message": "Can't find Project"})


if __name__ == '__main__':
    app.run()
