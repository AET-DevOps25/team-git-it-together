#!/usr/bin/env bash
set -euo pipefail

ENV_NAME="genai-devops25"
ENV_FILE="environment.yml"
REQ_FILE="requirements.txt"

show_help() {
  cat <<EOF
Usage: ${0##*/} [conda|venv]

  conda   Create/update a Conda env named '${ENV_NAME}'
  venv    Create/update a Python venv at '.venv/'
EOF
  exit 1
}

info()    { echo -e "🛠  $*"; }
success() { echo -e "✅  $*"; }
error()   { echo -e "❌  $*" >&2; exit 1; }

[[ $# -eq 1 ]] || show_help
MODE=$1

if [[ $MODE == "conda" ]]; then
  command -v conda >/dev/null || error "Conda not found. Please install Miniconda or Anaconda."
  [[ -f $ENV_FILE ]]   || error "'$ENV_FILE' not found in project root."

  if conda info --envs | awk '{print $1}' | grep -qx "$ENV_NAME"; then
    info "Updating existing Conda env '$ENV_NAME'..."
    conda env update -n "$ENV_NAME" -f "$ENV_FILE"
  else
    info "Creating Conda env '$ENV_NAME' from '$ENV_FILE'..."
    conda env create -n "$ENV_NAME" -f "$ENV_FILE"
  fi

  info "To activate, run:"
  echo "    conda init && source ~/.bashrc && conda activate $ENV_NAME"
  success "Conda setup complete."

elif [[ $MODE == "venv" ]]; then
  [[ -f $REQ_FILE ]]   || error "'$REQ_FILE' not found in project root."

  if [[ -d .venv ]]; then
    info "'.venv/' already exists. Skipping creation."
  else
    info "Creating Python venv at '.venv/'..."
    python -m venv .venv
  fi

  source .venv/bin/activate
  info "Installing/upgrading pip & dependencies from '$REQ_FILE'..."
  pip install --upgrade pip
  pip install -r "$REQ_FILE"
  success "venv ready! Activate via: source .venv/bin/activate"

else
  show_help
fi
