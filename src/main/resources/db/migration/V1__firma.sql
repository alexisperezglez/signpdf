CREATE TABLE configuracion
(
    id bigserial
        CONSTRAINT configuracion_pk
            PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL,
    imagen text,
    create_at TIMESTAMP DEFAULT now(),
    updtaed_at TIMESTAMP DEFAULT now()
);

CREATE UNIQUE INDEX configuracion_usuario_uindex
    ON configuracion (usuario);
