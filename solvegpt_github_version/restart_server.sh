pkill uvicorn
kill $(lsof -t -i:8000)

cp index.html /var/www/solvegpt/

nohup uvicorn main:app --host 127.0.0.1 --port 8000 > fastapi.log 2>&1 &