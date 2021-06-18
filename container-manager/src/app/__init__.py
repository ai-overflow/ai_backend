"""Flask Setup File
This file will setup Flask with CORS be enable remote requests
"""

from flask import Flask

app = Flask(__name__)

from app import views