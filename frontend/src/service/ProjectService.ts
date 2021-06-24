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
}

export default new ProjectService();