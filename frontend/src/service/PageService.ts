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

    private static readonly API_PATH = '/api/v1/pa/';

    public getAllPages(options?: any) {
        return axios
            .get(PageService.API_PATH + "page/", parseOptionsToAxiosConfig(options));
    }

    public getPage(id: string, options?: any) {
        return axios
            .get(PageService.API_PATH + "page/" + id, parseOptionsToAxiosConfig(options));
    }

    public deletePage(id: string, options?: any) {
        return axios
            .delete(PageService.API_PATH + "page/" + id, parseOptionsToAxiosConfig(options));
    }

    public addPage(info: PageType, options?: any) {
        options = {
            ...options, ...info
        };

        return axios
            .post(PageService.API_PATH + "page/", parseOptionsToAxiosConfig(options));
    }

    public updatePage(id: string, info: PageType, options?: any) {
        options = {
            ...options, ...info
        };

        return axios
            .put(PageService.API_PATH + "page/" + id, parseOptionsToAxiosConfig(options));
    }
}

export default new PageService();