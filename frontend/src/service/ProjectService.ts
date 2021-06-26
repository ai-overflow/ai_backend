import axios from 'axios';
import { parseOptionsToAxiosConfig } from './ServiceUtils';

class ProjectService {

    private static readonly API_PATH = '/api/v1/p/';

    public getAllProjects(options?: any) {
        return axios
            .get(ProjectService.API_PATH + "project/", parseOptionsToAxiosConfig(options));
    }

    public getProject(id: string, options?: any) {
        return axios
            .get(ProjectService.API_PATH + "project/" + id, parseOptionsToAxiosConfig(options));
    }

    public deleteProject(id: string, options?: any) {
        return axios
            .delete(ProjectService.API_PATH + "project/" + id, parseOptionsToAxiosConfig(options));
    }

    public addProject(url: string, options?: any) {
        options = {...options, repoUrl: url};

        return axios
            .post(ProjectService.API_PATH + "project/", parseOptionsToAxiosConfig(options));
    }

    public reloadProject(id: string, url: string, options?: any) {
        options = {...options, repoUrl: url};

        return axios
            .put(ProjectService.API_PATH + "project/" + id + "/reload", parseOptionsToAxiosConfig(options));
    }

    public updateProject(id: string, changes: {repoUrl: string, name: string, description: string}, options?: any) {
        options = {...options, ...changes};

        return axios
            .put(ProjectService.API_PATH + "project/" + id, parseOptionsToAxiosConfig(options));
    }
}

export default new ProjectService();