import axios from 'axios';
import { parseOptionsToAxiosConfig } from './ServiceUtils';

class ProjectService {

    private static readonly API_PATH = '/api/v1/p/';

    public getAllProjects(options?: any) {
        return axios
            .get(ProjectService.API_PATH + "project/", parseOptionsToAxiosConfig(options));
    }
}

export default new ProjectService();