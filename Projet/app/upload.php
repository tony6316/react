<?php
// Connexion à la base de données PostgreSQL
$dsn = "pgsql:host=db;port=5432;dbname=files;user=postgres;password=postgres";
try {
    $pdo = new PDO($dsn);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Erreur de connexion à la base de données : " . $e->getMessage());
}

// Traitement de l'envoi du fichier
$target_dir = "uploads/";
$target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
    echo "Le fichier " . basename($_FILES["fileToUpload"]["name"]) . " a été téléchargé avec succès.";

    // Insertion des informations dans la base de données
    $stmt = $pdo->prepare("INSERT INTO file_uploads (file_name) VALUES (:file_name)");
    $stmt->execute(['file_name' => basename($_FILES["fileToUpload"]["name"])]);
} else {
    echo "Désolé, une erreur s'est produite lors du téléchargement du fichier.";
}
