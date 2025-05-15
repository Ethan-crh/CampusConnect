# Configuration
$SSH_USER = "toto"
$SSH_HOST_BDD = "172.31.249.176"
$SSH_PASSWORD = "toto"
$SQL_PASSWORD = "votre_mot_de_passe"
$BDD_NAME = "BDD"

$SCP_USER = "toto"
$SCP_HOST = "172.31.253.120"
$SCP_PASSWORD = "toto"

$SOURCE_FILE = "C:\Sirius\CampusConnect\xmart-city-backend\target\xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"
$DEST_PATH = "/home/toto/SIRUIS"
$PORT = 45065

$FRONT_JAR_PATH = "C:\Sirius\CampusConnect\xmart-frontend\target\xmart-frontend-1.0-SNAPSHOT-jar-with-dependencies.jar"

# Verifie que plink (ou sshpass) est installe
if (-not (Get-Command "plink" -ErrorAction SilentlyContinue)) {
    Write-Host "Plink (ou un autre client SSH) est requis mais non installe."
    exit 1
}

# Compilation Maven
Write-Host "`n=== Compilation Maven ==="
Write-Host "N'oubliez pas de bien vous connecter au VPN"
Write-Host "Chargement en cours ..."

$compileResult = mvn clean package
if ($?) {
    Write-Host "Compilation reussie."
} else {
    Write-Host "Erreur de compilation Maven."
    exit 1
}

<# Verification de l'utilisation du port
Write-Host "`n=== Verification de l'utilisation du port $PORT ==="
$processID = plink -batch -ssh "$SCP_USER@$SCP_HOST" -pw $SCP_PASSWORD "ss -tulnp | findstr ':$PORT'"

if ($processID) {
    Write-Host "Port $PORT occupe. Killing..."
    plink -batch -ssh "$SCP_USER@$SCP_HOST" -pw $SCP_PASSWORD "kill -9 $processID"
    Write-Host "Processus tue."
} else {
    Write-Host "Le port $PORT est libre."
}

Start-Sleep -Seconds 3

# Suppression de l'ancien jar
Write-Host "`n=== Suppression de l'ancien fichier JAR ==="
plink -batch -ssh "$SCP_USER@$SCP_HOST" -pw $SCP_PASSWORD "rm -f ~/SIRUIS/xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"

# Envoi du nouveau fichier
Write-Host "`n=== Envoi du fichier JAR ==="
scp "$SOURCE_FILE" "${SCP_USER}@${SCP_HOST}:${DEST_PATH}"
if ($?) {
    Write-Host "Fichier envoye avec succes."
} else {
    Write-Host "Echec de l'envoi du fichier JAR."
    exit 1
}#>

# Demarrage du backend
Write-Host "`n=== Demarrage du backend sur la VM ==="
plink -batch -ssh "$SCP_USER@$SCP_HOST" -pw $SCP_PASSWORD "cd SIRUIS && nohup java -jar xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar > backend.log 2>&1 &"
Write-Host "Backend lance."

Start-Sleep -Seconds 5

# Demarrage du frontend localement
Write-Host "`n=== Demarrage du frontend localement ==="
if (Test-Path $FRONT_JAR_PATH) {
    Start-Process java -ArgumentList "-jar", $FRONT_JAR_PATH
    Write-Host "Frontend demarre."
} else {
    Write-Host "Fichier frontend non trouve a l'emplacement : $FRONT_JAR_PATH"
}
