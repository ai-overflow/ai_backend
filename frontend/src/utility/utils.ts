export function intersection(...arrays: Array<any>): Array<any> {
    if(arrays.length === 0) return [];

    let intersectionSet = arrays[0];
    for (const array of arrays) {
        intersectionSet = intersectionSet.filter((value: any) => array.includes(value));
    }

    return intersectionSet;
}
