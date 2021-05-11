# maindecoder
Hi! I am Russian software engineer. Team Lead. Video about me https://vk.com/wall-73375965_14179 I wrote this program, especially for Google, as it had crashes and did not work for more than an hour due to an overflowing data store. With this example, I want to show Google developers and get an idea of organizing decoding of large video files and working with cloud storage.

In detail, what would the program work I will describe tomorrow May 11, 2021

1. On the server Centos 7 you need to install:
2. 
//INSTALL AND CASTOM POSTGRESQL

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
    
    
    
//INSTALL AND CASTOM CRON

crontab -e

write a line to run jar file every 10 minutes and create a log file

*/1 * * * * flock -n /usr/share/nginx/html/maindecoder/uploads/lockfile /usr/bin/java -jar /usr/share/nginx/html/MainDecoder/target/decoder.jar > /home/logcron/cron.log 2>&1



//INSTALL FFMPEG

sudo rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm

sudo yum install ffmpeg ffmpeg-devel



    
