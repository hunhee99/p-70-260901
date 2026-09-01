'use client';

import { fetchApi } from "@/lib/client";
import { useRouter } from "next/navigation";
import { ReactEventHandler } from "react";

export default function Write() {

    const router = useRouter();

    const onSubmitHandle = (e: any) => {
        e.preventDefault();

        const form = e.target;

        const titleValue = form.title.value;
        const contentValue = form.content.value;

        if(titleValue.length === 0) {
            alert("제목을 입력해주세요");
            form.title.focus();
            return;
        }

        if(contentValue.length === 0) {
            alert("내용을 입력해주세요");
            form.content.focus();
            return;
        }

        fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/posts`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                "title": titleValue,
                "content" : contentValue
              })
        })
        .then(res => res.json())
        .then(data => {
            console.log(data);
            alert(data.msg);
            router.replace(`/posts/${data.data.id}`)
        });

    }

    return (
        <div>
            <div className="flex justify-center">
                <h1>글 작성 페이지</h1>
            </div>
            <form
                onSubmit={onSubmitHandle} 
                className="flex flex-col gap-4">
                <input 
                className="p-2 border-2 rounded"
                type="text" 
                name="title" 
                placeholder="제목을 입력해주세요"/>
                <textarea
                    className="p-2 border-2 rounded"
                    placeholder="내용을 입력해주세요"
                    name="content"
                ></textarea>
                <input className="p-2 border-2 rounded" type="submit" value="등록"/>
            </form>
        </div>
    );
}