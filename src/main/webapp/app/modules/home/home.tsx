import React, { FC } from 'react';
import { Alert } from 'reactstrap';
import './home.scss';

const InicioInformativo: FC = () => {
  return (
    <div className="inicio-informativo">
      {/* ✅ Mensaje de bienvenida (reemplaza usuarios predeterminados) */}
      <Alert color="info" className="text-center">
        Bienvenido a <strong>GymTrack</strong> 💪 <br />
        Gestiona tu entrenamiento y alcanza tus objetivos.
      </Alert>

      <div className="imagen-encabezado">
        <img src="../../../content/images/logotrans.png" alt="GymTrack Logo" className="logo-inicio" />
      </div>

      <div className="contenido-info">
        <div className="texto-info">
          <h4 className="subtitulo">Nuestros Servicios</h4>
          <h2 className="titulo">GYMTRACK</h2>
          <p className="descripcion">
            Lorem ipsum dolor sit amet consectetur adipisicing elit. Deleniti expedita facere, reprehenderit repudiandae quo incidunt
            aperiam voluptates illo delectus fugit. Assumenda fugiat veniam dolore aliquam dolor dolorem vel quidem possimus?
          </p>

          <h3 className="ofrecemos">Qué ofrecemos:</h3>
          <div className="ofertas">
            <div className="card-oferta">
              <p className="grande">
                24
                <br />
                <span className="pequeño">/7</span>
              </p>
              <p>Gestión</p>
            </div>

            <div className="card-oferta">
              <p>1 a 1</p>
              <p>Con entrenadores</p>
            </div>

            <div className="card-oferta">
              <p>Clases</p>
              <p>Personalizadas</p>
            </div>
          </div>
        </div>

        <div className="imagen-chica">
          <img src="../../../content/images/chica_fitness.png" alt="Chica Fitness" />
        </div>
      </div>
    </div>
  );
};

export default InicioInformativo;
