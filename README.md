# Maindecoder

Hello! I am Russian software engineer. Video about me https://vk.com/wall-73375965_14179

Awarded an exclusive gift by the Microsoft Director at the presentation MSSQL server in Moscow for the best question in the field of IT technologies  - asked to the American company Microsoft.
 
With this example, I want to show developers fullstark JAVA SE and get an idea of organizing decoding of large video files MP4 and working with cloud storage.

Here's what you need to run the program.

1. On the server Centos 7 you need to install:
 
//  INSTALL AND CASTOM POSTGRESQL

sudo rpm -Uvh  http://download.postgresql.org/pub/repos/yum/10/redhat/rhel-7.6-x86_64/pgdg-centos10-10-2.noarch.rpm

sudo yum install postgresql11-server postgresql11

CREATE TABLE public.medias

(

    id integer NOT NULL DEFAULT nextval('medias_id_seq'::regclass),
    
    lot_id bigint,
    
    order_id integer,
    
    media character varying(255) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    
    size integer,
    
    user_id integer,
    
    type_id integer,
    
    state_id integer,
    
    created_at timestamp(0) without time zone DEFAULT NULL::timestamp without time zone,
    
    updated_at timestamp(0) without time zone DEFAULT NULL::timestamp without time zone,
    
    deleted_at timestamp(0) without time zone DEFAULT NULL::timestamp without time zone,
    
    mda_lot_type_id integer,
    
    sort integer,
    
    mediavideo character varying COLLATE pg_catalog."default",
    
    split integer DEFAULT 0,
    
    scale integer,
    
    name character varying COLLATE pg_catalog."default",
    
    CONSTRAINT medias_pkey PRIMARY KEY (id)
    
)

WITH (

    OIDS = FALSE
    
)

TABLESPACE pg_default;

ALTER TABLE public.medias

    OWNER to postgres;
    
    
    
//  INSTALL AND CASTOM CRON

crontab -e

write a line to run jar file every 10 minutes and create a log file

*/1 * * * * flock -n /usr/www/html/maindecoder/uploads/lockfile /usr/bin/java -jar /usr/share/nginx/html/MainDecoder/target/decoder.jar > /home/logcron/cron.log 2>&1



//  INSTALL FFMPEG

sudo yum install epel-release

sudo yum localinstall --nogpgcheck https://download1.rpmfusion.org/free/el/rpmfusion-free-release-7.noarch.rpm

sudo yum install ffmpeg ffmpeg-devel

ffmpeg -version

ffmpeg version 3.4.8 Copyright (c) 2000-2020 the FFmpeg developers

built with gcc 4.8.5 (GCC) 20150623 (Red Hat 4.8.5-39)

3.4.8 - version works stably with files over 300MB

//  INSTALL JDK 8 

sudo yum install java-1.8.0-openjdk

2. The decoder works in single-threaded mode, but can be used in multi-threaded mode on JAVA SPRING.

for this you need to add the frontend and backend. if you want to quickly write a service like YOUTUBE.

Frontend are very good for this plyr and dropzonejs

Display video files https://atuin.ru/blog/media-plejer-plyr-dlya-html5-youtube-i-vimeo/ https://github.com/sampotts/plyr

Uploading video files https://www.dropzonejs.com/

For the backend, you can use the same Amazon S3, what's in the pom file.

Do not forget to include in the project postgresql-42.2.1.jar.

It is not necessary to use this program on a server if you have a powerful home computer. You can install UBUNTU and compress files.

MAINDECODER checked by me on UBUNTU works.

The program works perfectly without bugs. 

Good luck everyone. 

Together we make our world the best!















    
