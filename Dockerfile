FROM openjdk:17-jdk-slim

# build/libs/board-0.0.1-SNAPSHOT.jar board.jar이란 이름으로 파일을 docker image 디렉토리에 저장
COPY build/libs/board-0.0.1-SNAPSHOT.jar board.jar

# 위 명령어에서 저장한 'board.jar' 파일과 더불어 프로젝트를 실행할 명령어 설정
ENTRYPOINT ["java","-jar","board.jar"]