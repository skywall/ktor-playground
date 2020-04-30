build: Dockerfile
	docker build -t micro-funspace .

run:
	docker run -p 8080:8080 micro-funspace