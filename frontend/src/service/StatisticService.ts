import axios from 'axios';
import { parseOptionsToAxiosConfig } from './ServiceUtils';


class StatisticService {

    private static readonly API_PATH = '/api/v1/st/';

    public getProjectStats(id: string, options?: any) {
        return axios
            .get(StatisticService.API_PATH + "project/" + id, parseOptionsToAxiosConfig(options));
    }
}

export default new StatisticService();