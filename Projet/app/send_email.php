<?php
$to = "test@example.com";
$subject = "Test d'envoi d'email avec MailHog";
$message = "Ceci est un test pour vérifier l'envoi d'email via MailHog.";
$headers = "From: no-reply@example.com";

if (mail($to, $subject, $message, $headers)) {
    echo "Email envoyé avec succès.";
} else {
    echo "Erreur lors de l'envoi de l'email.";
}
?>
