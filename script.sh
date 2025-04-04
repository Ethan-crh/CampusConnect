#!/bin/bash

#VM-BDD
SSH_USER="toto"
SSH_HOST="172.31.249.176"
SSH_PASSWORD="toto"
SQL_PASSWORD="votre_mot_de_passe"
BDD_NAME="BDD"

#Back->VM-BACK
SCP_USER="toto"
SCP_HOST="172.31.253.120"
SOURCE_FILE="xmart-city-backend/target/xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"
DEST_PATH="/home/toto/SIRUIS"
SCP_PASSWORD="toto"

# Connexion BDD
osascript -e "tell application \"Terminal\" to do script \"sshpass -p 'toto' ssh -o StrictHostKeyChecking=no toto@172.31.249.176 && mysql -u root -p\""

# Compiler le projet Maven
echo "Compliation Maven"
EOF
echo "Mvn clean package"

mvn clean package
if [ $? -ne 0 ]; then
    echo "compilation erreur!"
    exit 1
fi

echo "compilation réussie."

# dispo du port 45065 est kill si necessaire
PORT=45065
PID=$(sshpass -p "$SCP_PASSWORD" ssh -o StrictHostKeyChecking=no "$SCP_USER@$SCP_HOST" "ss -tulnp | grep ':$PORT' | grep -oP '(?<=pid=)[0-9]+'")

if [ ! -z "$PID" ]; then
    echo "$PORT est occupé par PID: $PID."
    echo "killing.."
    sshpass -p "$SCP_PASSWORD" ssh "$SCP_USER@$SCP_HOST" "kill -9 $PID"
    echo "Killed."
else
    echo "Le port $PORT est libre."
fi

#petite pause
sleep 3

#SCP pour envoyer le fichier

echo "--------------------Suppression des anciens fichiers et dossiers"
sshpass -p "$SCP_PASSWORD" ssh -o StrictHostKeyChecking=no "$SCP_USER@$SCP_HOST" "rm -rf ~/SIRUIS/xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar "

echo "--------------------Envoi du fichier jar Backend...--------------------"
sshpass -p "$SCP_PASSWORD" scp "$SOURCE_FILE" "$SCP_USER@$SCP_HOST:$DEST_PATH"

if [ $? -ne 0 ]; then
    echo "Envoi du fichier échoué !"
    exit 1
fi
echo "Fichier envoyé!!!!."


# Lancement du back
echo "demmarage du backend"
osascript -e 'tell application "Terminal" to do script "sshpass -p '\''toto'\'' ssh -o StrictHostKeyChecking=no toto@172.31.253.120 \"cd SIRUIS && java -jar xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar\" "'
echo "Back-end démarré."


#Une autre petite pause pour attendre le back youpii on est presque !!
sleep 5

#Lancement du Front
echo "demmarage du front"
osascript -e 'tell application "Terminal" to do script "cd ~/desktop/OutilsCycleDeVieDeLogiciel/CampusConnect/xmart-frontend/target && java -jar xmart-frontend-1.0-SNAPSHOT-jar-with-dependencies.jar"'

echo "Front-End démarré."