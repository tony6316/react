<?php
$file = $_FILES['file'];
$fileName = uniqid() . "_" . $file['name'];
move_uploaded_file($file['tmp_name'], "uploads/" . $fileName);

// Connexion à PostgreSQL et stockage des informations sur le fichier
$conn = pg_connect("host=db dbname=postgres user=postgres password=postgres");
$query = "INSERT INTO uploads (file_name, upload_time) VALUES ('$fileName', NOW())";
pg_query($conn, $query);
?>
