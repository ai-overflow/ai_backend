# AI backend

This project is the main component of AI Overflow. 

## Backend

The backend is a Java Spring Application which manages all requests with a REST Interface. 

## Manager

The manager will accept all requests to change the state or getting the status of a managed container. This container will have elevated privileges as it needs them to access the docker socket.

## Frontend

The frontend is a Vue application which will represent the contents coming from the backend in an pleasant to look at way.
