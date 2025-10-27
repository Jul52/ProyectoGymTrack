import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/payment">
        <Translate contentKey="global.menu.entities.payment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/user-data">
        <Translate contentKey="global.menu.entities.userData" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/document-type">
        <Translate contentKey="global.menu.entities.documentType" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reservation">
        <Translate contentKey="global.menu.entities.reservation" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/machine">
        <Translate contentKey="global.menu.entities.machine" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/incident">
        <Translate contentKey="global.menu.entities.incident" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/machine-incidents">
        <Translate contentKey="global.menu.entities.machineIncidents" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/category">
        <Translate contentKey="global.menu.entities.category" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/payment-method">
        <Translate contentKey="global.menu.entities.paymentMethod" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/gym-service">
        <Translate contentKey="global.menu.entities.gymService" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/invoice">
        <Translate contentKey="global.menu.entities.invoice" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/invoice-service">
        <Translate contentKey="global.menu.entities.invoiceService" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/course">
        <Translate contentKey="global.menu.entities.course" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/schedule">
        <Translate contentKey="global.menu.entities.schedule" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/zone">
        <Translate contentKey="global.menu.entities.zone" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
