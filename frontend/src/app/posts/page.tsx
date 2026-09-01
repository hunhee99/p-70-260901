'use client';
import { fetchApi } from "@/lib/client";
import { PostDto } from "@/type/post";
import Link from "next/link";
import { useEffect, useState } from "react";

export default function Posts() {

    const [posts, setPosts] = useState<PostDto[]>([]);

    useEffect(() => {
        fetchApi('/api/v1/posts')
            .then(setPosts);
    }, []);


    return (
        <>
            {posts.length === 0
                ? <div>로딩중..</div>
                :<div className="flex flex-col gap-4 items-center"> 
                    <ul>
                        {posts.map((post) => (
                            <Link key={post.id} href={`/posts/${post.id}`}>
                                <li>
                                    {post.id} : {post.title}
                                </li>
                            </Link>
                        ))}
                    </ul>
                    <Link href="/posts/write">
                        글 작성
                    </Link>
                </div>
            }
        </>);
}
