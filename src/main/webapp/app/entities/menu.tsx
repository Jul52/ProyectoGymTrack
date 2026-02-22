import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const EntitiesMenu = () => {
  const account = useAppSelector(state => state.authentication.account);

  // 1. Identificación de Roles
  const isAdmin = hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN]);
  const isTrainer = hasAnyAuthority(account.authorities, ['ROLE_TRAINER']);
  const isUser = hasAnyAuthority(account.authorities, [AUTHORITIES.USER]);

  // 2. Lógica de visibilidad por secciones

  // Lo que ve el Admin (Configuración técnica)
  const showAdminOnly = isAdmin;

  // Lo que ve el Trainer (Solo sus 4 áreas específicas)
  const showTrainerEntities = isAdmin || isTrainer;

  // Lo que ve el Usuario (Reservas y finanzas)
  // IMPORTANTE: Aquí excluimos al Trainer explícitamente si no es Admin
  const showUserEntities = isAdmin || (isUser && !isTrainer);

  return (
    <>
      {/* --- SECCIÓN TÉCNICA (Solo Admin) --- */}
      {showAdminOnly && (
        <>
          <MenuItem icon="asterisk" to="/user-data">
            <Translate contentKey="global.menu.entities.userData" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/document-type">
            <Translate contentKey="global.menu.entities.documentType" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/category">
            <Translate contentKey="global.menu.entities.category" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/payment-method">
            <Translate contentKey="global.menu.entities.paymentMethod" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/machine">
            <Translate contentKey="global.menu.entities.machine" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/incident">
            <Translate contentKey="global.menu.entities.incident" />
          </MenuItem>
        </>
      )}

      {/* --- RESERVACIONES --- */}
      {showUserEntities && (
        <MenuItem icon="asterisk" to="/reservation">
          <Translate contentKey="global.menu.entities.reservation" />
        </MenuItem>
      )}

      {/* --- ZONAS --- */}
      {showTrainerEntities && (
        <MenuItem icon="asterisk" to="/zone">
          <Translate contentKey="global.menu.entities.zone" />
        </MenuItem>
      )}

      {/* --- INCIDENTES DE MÁQUINA (Admin, Trainer y ahora User) --- */}
      {(isAdmin || isTrainer || isUser) && (
        <MenuItem icon="asterisk" to="/machine-incidents">
          <Translate contentKey="global.menu.entities.machineIncidents" />
        </MenuItem>
      )}

      {/* --- SERVICIOS DEL GYM --- */}
      {showUserEntities && (
        <MenuItem icon="asterisk" to="/gym-service">
          <Translate contentKey="global.menu.entities.gymService" />
        </MenuItem>
      )}

      {/* --- FACTURAS --- */}
      {showUserEntities && (
        <MenuItem icon="asterisk" to="/invoice">
          <Translate contentKey="global.menu.entities.invoice" />
        </MenuItem>
      )}

      {/* --- HORARIOS Y CURSOS (Visible para todos los roles) --- */}
      {(isAdmin || isTrainer || isUser) && (
        <>
          <MenuItem icon="asterisk" to="/schedule">
            <Translate contentKey="global.menu.entities.schedule" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/course">
            <Translate contentKey="global.menu.entities.course" />
          </MenuItem>
        </>
      )}

      {/* --- PAGOS --- */}
      {showUserEntities && (
        <MenuItem icon="asterisk" to="/payment">
          <Translate contentKey="global.menu.entities.payment" />
        </MenuItem>
      )}
    </>
  );
};

export default EntitiesMenu;
