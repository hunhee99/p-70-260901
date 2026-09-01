'use client';
import { fetchApi } from "@/lib/client";
import { PostDto } from "@/type/post";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react"

export default function Detail() {

    const {id} = useParams();
    const router = useRouter();
    const [post, setPost] = useState<PostDto | null>(null);

    useEffect(() => {

        fetchApi(`/api/v1/posts/${id}`)
        .then(setPost)

    }, []);

    const onDeleteHandle = () => {

        fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/posts/${id}`, {
            method: "DELETE",
        })
        .then(res => res.json())
        .then(data => {
            alert(data.msg);
            router.replace("/posts");
        });
    }

    if(post === null) {
        return <div>로딩중...</div>
    }

    return (
        <div>
            <h1>상세 페이지</h1>
            <div>번호 : {post.id}</div>
            <div>제목 : {post.title}</div>
            <div>내용 : {post.content}</div>
            <div className="flex gap-4">
                <Link href={`/posts/${post.id}/edit`}>수정</Link>
                <button onClick={onDeleteHandle}>삭제</button>
            </div>
        </div>
    )
}
