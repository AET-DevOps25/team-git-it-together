FROM python:3.12-slim

# Create and switch to a non-root user
RUN useradd --create-home appuser
USER appuser

WORKDIR /home/appuser/app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy source
COPY --chown=appuser:appuser . .

# Expose port (make sure your app reads $PORT)
ENV PORT=8082
EXPOSE ${PORT}

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/health || exit 1

ENTRYPOINT ["python", "src/main.py"]
