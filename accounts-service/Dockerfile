FROM python:3.9-slim-bullseye

RUN python3 -m venv /opt/venv

# Install dependencies:
COPY requirements.txt .

RUN /opt/venv/bin/pip install -r requirements.txt

# Run the application:
COPY app .

CMD ["/opt/venv/bin/python", "main.py"]