# Utiliser l'image officielle PHP avec Apache
FROM php:8.0-apache

# Copier le fichier de configuration personnalisé dans l'image
COPY ./conf/php.ini /usr/local/etc/php/php.ini

# Installer les dépendances nécessaires, y compris libpq-dev pour pdo_pgsql
RUN apt-get update && apt-get install -y curl unzip libpq-dev \
    && docker-php-ext-install pdo pdo_pgsql

# Installer Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Copier les fichiers de l'application dans le conteneur
COPY ./app /var/www/html

# Exposer le port 80 pour Apache
EXPOSE 80
