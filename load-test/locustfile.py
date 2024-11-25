import random

from locust import task, FastHttpUser

class Post(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0
    @task
    def getPostList(self):
        jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NCIsImF1dGgiOiJVU0VSIiwiZXhwIjoxNzI1NjgxODI2fQ.n2mqM8q82Z2dgl4YGGgpUJIFpMzeg-HvT_F8nO1PJOxPUwumQGQyiqfOuwyAkWa8vJ9edg19VDdMCrAJX0bk7w"
        headers = {"Authorization": "Bearer " + jwt}

        self.client.get("/post/list?page=3&count=100", headers=headers)

    @task
    def addComment(self):
        jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NCIsImF1dGgiOiJVU0VSIiwiZXhwIjoxNzI1NjgxODI2fQ.n2mqM8q82Z2dgl4YGGgpUJIFpMzeg-HvT_F8nO1PJOxPUwumQGQyiqfOuwyAkWa8vJ9edg19VDdMCrAJX0bk7w"
        headers = {"Authorization": "Bearer " + jwt}

        # POST 요청 보내기
        json_body = {
            "content": "{'verbal':[5,3,5,5,5],'nonVerbal':[4,5,4,5],'review':'1221'}",
            "userId": 1
        }
        self.client.post("/post/91/comment", json=json_body, headers=headers)

