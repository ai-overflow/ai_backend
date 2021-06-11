import axios from 'axios';
import { parseOptionsToAxiosConfig } from './ServiceUtils';

export interface PageType {
    description: string,
    title: string,
    active: boolean,
    selectedProjects: Array<number>,
    topLevelInput: Record<string, string>,
}

class PageService {

    private static readonly API_PATH = '/api/v1/public/pa/';

    public getAllPages(options?: any) {
        return axios
            .get(PageService.API_PATH + "page/", parseOptionsToAxiosConfig(options));
    }

    public getPage(id: string, options?: any) {
        return axios
            .get(PageService.API_PATH + "page/" + id, parseOptionsToAxiosConfig(options));
    }
}

export default new PageService();