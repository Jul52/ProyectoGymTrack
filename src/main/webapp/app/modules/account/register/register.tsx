import React, { useEffect, useState } from 'react';
import { Translate, ValidatedField, ValidatedForm, isEmail, translate } from 'react-jhipster';
import { Alert, Button, Col, Row } from 'reactstrap';
import { toast } from 'react-toastify';
import { Link } from 'react-router-dom';
import './register.scss';

import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { handleRegister, reset } from './register.reducer';

export const RegisterPage = () => {
  const [password, setPassword] = useState('');
  const dispatch = useAppDispatch();

  useEffect(() => {
    return () => {
      dispatch(reset());
    };
  }, [dispatch]);

  const currentLocale = useAppSelector(state => state.locale.currentLocale);
  const successMessage = useAppSelector(state => state.register.successMessage);

  const handleValidSubmit = formValues => {
    dispatch(
      handleRegister({
        login: formValues.username,
        email: formValues.email,
        password: formValues.firstPassword,
        langKey: currentLocale,

        // ✅ campos personalizados
        tipoDocumento: formValues.tipoDocumento,
        numeroDocumento: formValues.numeroDocumento,
        fechaNacimiento: formValues.fechaNacimiento,
      }),
    );
  };

  const updatePassword = event => setPassword(event.target.value);

  useEffect(() => {
    if (successMessage) {
      toast.success(translate(successMessage));
    }
  }, [successMessage]);

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <ValidatedForm id="register-form" onSubmit={handleValidSubmit}>
            <h1 id="register-title">
              <Translate contentKey="register.title">Registro</Translate>
            </h1>

            {/* USUARIO */}
            <ValidatedField
              name="username"
              label={translate('global.form.username.label')}
              placeholder={translate('global.form.username.placeholder')}
              validate={{
                required: { value: true, message: translate('register.messages.validate.login.required') },
                pattern: {
                  value: /^[_.@A-Za-z0-9-]+$/,
                  message: translate('register.messages.validate.login.pattern'),
                },
                minLength: { value: 1, message: translate('register.messages.validate.login.minlength') },
                maxLength: { value: 50, message: translate('register.messages.validate.login.maxlength') },
              }}
            />

            {/* EMAIL */}
            <ValidatedField
              name="email"
              label={translate('global.form.email.label')}
              placeholder={translate('global.form.email.placeholder')}
              type="email"
              validate={{
                required: { value: true, message: translate('global.messages.validate.email.required') },
                minLength: { value: 5, message: translate('global.messages.validate.email.minlength') },
                maxLength: { value: 254, message: translate('global.messages.validate.email.maxlength') },
                validate: v => isEmail(v) || translate('global.messages.validate.email.invalid'),
              }}
            />

            {/* CONTRASEÑA */}
            <ValidatedField
              name="firstPassword"
              label={translate('global.form.newpassword.label')}
              placeholder={translate('global.form.newpassword.placeholder')}
              type="password"
              onChange={updatePassword}
              validate={{
                required: { value: true, message: translate('global.messages.validate.newpassword.required') },
                minLength: { value: 4, message: translate('global.messages.validate.newpassword.minlength') },
                maxLength: { value: 50, message: translate('global.messages.validate.newpassword.maxlength') },
              }}
            />

            {/* 🔹 CAMPOS NUEVOS 🔹 */}

            <ValidatedField
              name="tipoDocumento"
              label="Tipo de documento"
              placeholder="CC, TI, CE..."
              validate={{
                required: { value: true, message: 'Este campo es obligatorio' },
              }}
            />

            <ValidatedField
              name="numeroDocumento"
              label="Número de documento"
              placeholder="Ingrese su documento"
              validate={{
                required: { value: true, message: 'Este campo es obligatorio' },
              }}
            />

            <ValidatedField
              name="fechaNacimiento"
              label="Fecha de nacimiento"
              type="date"
              validate={{
                required: { value: true, message: 'Este campo es obligatorio' },
              }}
            />

            <PasswordStrengthBar password={password} />

            {/* CONFIRMAR CONTRASEÑA */}
            <ValidatedField
              name="secondPassword"
              label={translate('global.form.confirmpassword.label')}
              placeholder={translate('global.form.confirmpassword.placeholder')}
              type="password"
              validate={{
                required: { value: true, message: translate('global.messages.validate.confirmpassword.required') },
                minLength: { value: 4, message: translate('global.messages.validate.confirmpassword.minlength') },
                maxLength: { value: 50, message: translate('global.messages.validate.confirmpassword.maxlength') },
                validate: v => v === password || translate('global.messages.error.dontmatch'),
              }}
            />

            <Button color="primary" type="submit">
              <Translate contentKey="register.form.button">Registrarse</Translate>
            </Button>
          </ValidatedForm>

          <p />

          <Alert color="warning">
            <Translate contentKey="global.messages.info.authenticated.prefix">Si deseas</Translate>{' '}
            <Link to="/login" className="alert-link">
              <Translate contentKey="global.messages.info.authenticated.link">iniciar sesión</Translate>
            </Link>
          </Alert>
        </Col>
      </Row>
    </div>
  );
};

export default RegisterPage;
