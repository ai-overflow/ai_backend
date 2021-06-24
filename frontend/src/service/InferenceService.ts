import axios from 'axios';
import { parseOptionsToAxiosConfig } from './ServiceUtils';

class InferenceService {

    private static readonly API_PATH = '/api/v1/in/';

    public activateModel(id: string, options?: any) {
        return axios
            .put(InferenceService.API_PATH + "activate/" + id, parseOptionsToAxiosConfig(options));
    }

    public deactivateModel(id: string, options?: any) {
        return axios
            .put(InferenceService.API_PATH + "deactivate/" + id, parseOptionsToAxiosConfig(options));
    }

    public getStatus(options?: any) {
        return axios
            .get(InferenceService.API_PATH + "status", parseOptionsToAxiosConfig(options));
    }
}

export default new InferenceService();