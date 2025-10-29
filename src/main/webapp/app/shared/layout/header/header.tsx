import './header.scss';

import React, { useState } from 'react';
import { Storage, Translate } from 'react-jhipster';
import { Collapse, Nav, Navbar, NavbarToggler } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';

import { useAppDispatch } from 'app/config/store';
import { setLocale } from 'app/shared/reducers/locale';
import { AccountMenu, AdminMenu, EntitiesMenu, LocaleMenu } from '../menus';
import { Brand, Home } from './header-components';
import { NavLink as Link } from 'react-router-dom';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
  currentLocale: string;
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);

  const dispatch = useAppDispatch();

  const handleLocaleChange = event => {
    const langKey = event.target.value;
    Storage.session.set('locale', langKey);
    dispatch(setLocale(langKey));
  };

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">
          <Translate contentKey={`global.ribbon.${props.ribbonEnv}`} />
        </a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);

  const CustomBrand = () => (
    <Link to="/" className="navbar-brand d-flex align-items-center">
      <img src="content/images/logo-gymtrack2.png" alt="GymTrack Logo" className="logo-img" />
      <span className="brand-title ms-2">
        <Translate contentKey="global.title">GYMTRACK</Translate>
      </span>
    </Link>
  );

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      <LoadingBar className="loading-bar" />
      <Navbar data-cy="navbar" expand="md" fixed="top" className="navbar-custom">
        <CustomBrand />
        <Collapse isOpen={menuOpen} navbar>
          <Nav id="header-tabs" className="ms-auto navbar-center-links" navbar>
            <Home />
            {props.isAuthenticated && <EntitiesMenu />}
            {props.isAuthenticated && props.isAdmin && <AdminMenu showOpenAPI={props.isOpenAPIEnabled} />}
            <LocaleMenu currentLocale={props.currentLocale} onClick={handleLocaleChange} />
            <AccountMenu isAuthenticated={props.isAuthenticated} />
          </Nav>
        </Collapse>
      </Navbar>
    </div>
  );
};

export default Header;
