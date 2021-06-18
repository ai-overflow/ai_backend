import axios from 'axios';
import { parseOptionsToAxiosConfig } from './ServiceUtils';

class DockerService {

    private static readonly API_PATH = '/api/v1/cp/';

    public getRunningContainers(options?: any) {
        return axios
            .get(DockerService.API_PATH + "container/", parseOptionsToAxiosConfig(options));
    }

    public stopContainer(id: string, options?: any) {
        return axios
            .delete(DockerService.API_PATH + "container/" + id, parseOptionsToAxiosConfig(options));
    }

    public startContainer(id: string, options?: any) {
        options = {...options};

        return axios
            .post(DockerService.API_PATH + "container/" + id, parseOptionsToAxiosConfig(options));
    }
}

export default new DockerService();