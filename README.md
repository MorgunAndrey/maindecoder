# Maindecoder

Hi! I am Russian software engineer. Team Lead. Video about me https://vk.com/wall-73375965_14179

I wrote this program specifically for Google as there were crashes and didn’t work for more than an hour due to data storage overflow.

Very sad. 

With this example, I want to show Google developers and get an idea of organizing decoding of large video files and working with cloud storage.

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

sudo rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm

sudo yum install ffmpeg ffmpeg-devel


//  INSTALL JDK 8 AND JRE 8

sudo yum install java-1.8.0-openjdk-devel

sudo yum install java-1.8.0-openjdk

2. The decoder works in single-threaded mode, but can be used in multi-threaded mode on JAVA SPRING,or C#, PHP, Python.

for this you need to add the frontend and backend. if you want to quickly write a service like YOUTUBE.

Frontend are very good for this plyr and dropzonejs

https://atuin.ru/blog/media-plejer-plyr-dlya-html5-youtube-i-vimeo/

https://www.dropzonejs.com/

For the backend, you can use the same Amazon S3, what's in the pom file.

Do not forget to include in the project postgresql-42.2.1.jar.

The program works perfectly without bugs. 

Good luck everyone. 

Together we make our world the best!















    
