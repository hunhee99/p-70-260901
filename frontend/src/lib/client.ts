export function fetchApi(url: string) {
    return fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}${url}`)
        .then((res) => res.json());
}