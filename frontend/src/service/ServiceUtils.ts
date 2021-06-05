

/*
    This methods provides an axios config object, that
    provides some default values, that can be modified with the
    options object
 */
export function parseOptionsToAxiosConfig(options: any) {
    const parsedConfig = {
        errorHandle: true
    };
    return Object.assign(parsedConfig, options);
}