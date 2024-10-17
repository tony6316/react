<?php
$files = scandir('uploads/');
?>
<h2>Fichiers téléchargés</h2>
<ul>
    <?php foreach ($files as $file) {
        if ($file !== '.' && $file !== '..') {
            echo "<li><a href='uploads/$file' target='_blank'>$file</a></li>";
        }
    } ?>
</ul>
