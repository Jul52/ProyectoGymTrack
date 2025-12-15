import React from 'react';
import { Translate, ValidatedField, translate } from 'react-jhipster';
import { Alert, Button, Col, Form, Modal, ModalBody, ModalFooter, ModalHeader, Row } from 'reactstrap';
import { Link } from 'react-router-dom';
import { type FieldError, useForm } from 'react-hook-form';
import './login-modal.scss'; // 👈 archivo nuevo para estilos específicos del modal

export interface ILoginModalProps {
  showModal: boolean;
  loginError: boolean;
  handleLogin: (username: string, password: string, rememberMe: boolean) => void;
  handleClose: () => void;
}

const LoginModal = (props: ILoginModalProps) => {
  const login = ({ username, password, rememberMe }) => {
    props.handleLogin(username, password, rememberMe);
  };

  const {
    handleSubmit,
    register,
    formState: { errors, touchedFields },
  } = useForm({ mode: 'onTouched' });

  const { loginError, handleClose } = props;

  const handleLoginSubmit = e => {
    handleSubmit(login)(e);
  };

  return (
    <Modal isOpen={props.showModal} toggle={handleClose} backdrop="static" id="login-page" autoFocus={false} className="login-modal">
      <Form onSubmit={handleLoginSubmit}>
        <ModalHeader id="login-title" data-cy="loginTitle" toggle={handleClose}>
          <Translate contentKey="login.title">Iniciar sesión</Translate>
        </ModalHeader>
        <ModalBody>
          <Row>
            <Col md="12">
              {loginError && (
                <Alert color="danger" data-cy="loginError" className="text-center fw-semibold">
                  <Translate contentKey="login.messages.error.authentication">
                    <strong>¡Error al iniciar sesión!</strong> Revisa tus credenciales e inténtalo de nuevo.
                  </Translate>
                </Alert>
              )}
            </Col>
            <Col md="12">
              <ValidatedField
                name="username"
                label={translate('global.form.username.label')}
                placeholder={translate('global.form.username.placeholder')}
                required
                autoFocus
                data-cy="username"
                validate={{ required: 'Username cannot be empty!' }}
                register={register}
                error={errors.username as FieldError}
                isTouched={touchedFields.username}
              />
              <ValidatedField
                name="password"
                type="password"
                label={translate('login.form.password')}
                placeholder={translate('login.form.password.placeholder')}
                required
                data-cy="password"
                validate={{ required: 'Password cannot be empty!' }}
                register={register}
                error={errors.password as FieldError}
                isTouched={touchedFields.password}
              />
              <ValidatedField
                name="rememberMe"
                type="checkbox"
                check
                label={translate('login.form.rememberme')}
                value={true}
                register={register}
              />
            </Col>
          </Row>

          <div className="mt-3">
            <Alert color="warning" className="p-2 text-center">
              <Link to="/account/reset/request" data-cy="forgetYourPasswordSelector" className="link-reset">
                <Translate contentKey="login.password.forgot">¿Olvidaste tu contraseña?</Translate>
              </Link>
            </Alert>
            <Alert color="warning" className="p-2 text-center">
              <span>
                <Translate contentKey="global.messages.info.register.noaccount">¿No tienes una cuenta todavía?</Translate>
              </span>{' '}
              <Link to="/account/register" className="link-register">
                <Translate contentKey="global.messages.info.register.link">Regístrate</Translate>
              </Link>
            </Alert>
          </div>
        </ModalBody>

        <ModalFooter className="d-flex justify-content-between">
          <Button onClick={handleClose} tabIndex={1} className="btn-login">
            <Translate contentKey="entity.action.cancel">Cancelar</Translate>
          </Button>
          <Button type="submit" data-cy="submit" className="btn-registrarse">
            <Translate contentKey="login.form.button">Iniciar sesión</Translate>
          </Button>
        </ModalFooter>
      </Form>
    </Modal>
  );
};

export default LoginModal;
