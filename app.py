from fastapi import FastAPI, Request, HTTPException
import json
from zhipuai import ZhipuAI

app = FastAPI()


@app.post("/parse_image/")
async def parse_image(request: Request):
    print("start parsing")
    try:
        # 直接从 body 中解析 JSON 数据
        body = await request.json()
        image_base64 = body.get("image_base64")
        api_key = body.get("api_key")

        if not image_base64 or not api_key:
            raise HTTPException(status_code=400, detail="Missing required parameters: image_base64 or api_key")

        # 处理逻辑
        prompt = """
        请解析这张图片中的选择题、填空题和判断题，其他类型的题目请忽略。解析完成后作答，填写相关内容。对于不存在的属性，请设为空值。返回解析结果为 JSON 列表，格式如下：
        选择题：{type: 1, question: '题目内容', answerA: '选项A', answerB: '选项B', answerC: '选项C', answerD: '选项D', rightAnswer: '正确答案(请填A,B,C,D不要直接填内容)', analysis: '解析内容'}
        填空题：{type: 2, question: '题目内容', answer: '正确答案', analysis: '解析内容'}
        判断题：{type: 3, question: '题目内容', answer: 'T/F', analysis: '解析内容'}
        注意：
        - 如果图片中题目内容模糊或无法完整识别，返回空 JSON 对象 '{}'。
        - 缺失的答案或解析请设为空字符串 ''。
        - 如果题目类型不符合要求，请忽略。
        """
        client = ZhipuAI(api_key=api_key)
        response = client.chat.completions.create(
            temperature=0,
            model="glm-4v-plus",
            messages=[
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": image_base64}},
                        {"type": "text", "text": prompt}
                    ]
                }
            ]
        )
        # print(response.choices[0].message.content)
        return response.choices[0].message.content

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == '__main__':
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8899)
