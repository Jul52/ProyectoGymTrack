import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { getEntities as getPaymentMethods } from 'app/entities/payment-method/payment-method.reducer';
import { getEntities as getUserData } from 'app/entities/user-data/user-data.reducer';
import { createEntity, getEntity, reset, updateEntity } from './payment.reducer';

const METHOD_ICONS: Record<string, string> = {
  nequi: '📱',
  daviplata: '💜',
  efectivo: '💵',
  'tarjeta de crédito': '💳',
  'tarjeta de debito': '💳',
  'tarjeta de débito': '💳',
  pse: '🏦',
  transferencia: '🔄',
};

const getMethodIcon = (name: string) => METHOD_ICONS[name?.toLowerCase()] ?? '💳';

const styles = {
  overlay: {
    position: 'fixed' as const,
    inset: 0,
    background: 'rgba(0,0,0,0.6)',
    backdropFilter: 'blur(4px)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000,
    animation: 'fadeIn 0.3s ease',
  },
  modal: {
    background: '#fff',
    borderRadius: '20px',
    padding: '40px',
    width: '100%',
    maxWidth: '460px',
    boxShadow: '0 25px 60px rgba(0,0,0,0.3)',
    animation: 'slideUp 0.4s cubic-bezier(0.34,1.56,0.64,1)',
  },
  header: { display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '28px' },
  logo: {
    width: '44px',
    height: '44px',
    background: 'linear-gradient(135deg, #1a73e8, #0d47a1)',
    borderRadius: '12px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '22px',
  },
  title: { margin: 0, fontSize: '20px', fontWeight: 700, color: '#1a1a2e' },
  subtitle: { margin: 0, fontSize: '13px', color: '#6b7280' },
  summaryBox: {
    background: 'linear-gradient(135deg, #f0f7ff, #e8f4fd)',
    border: '1px solid #bfdbfe',
    borderRadius: '14px',
    padding: '18px 20px',
    marginBottom: '24px',
  },
  summaryRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' },
  summaryLabel: { fontSize: '13px', color: '#6b7280' },
  summaryValue: { fontSize: '14px', fontWeight: 600, color: '#1a1a2e' },
  divider: { border: 'none', borderTop: '1px solid #bfdbfe', margin: '12px 0' },
  totalLabel: { fontSize: '15px', fontWeight: 600, color: '#1a1a2e' },
  totalValue: { fontSize: '22px', fontWeight: 800, color: '#1a73e8' },
  sectionTitle: {
    fontSize: '13px',
    fontWeight: 600,
    color: '#6b7280',
    textTransform: 'uppercase' as const,
    letterSpacing: '0.5px',
    marginBottom: '10px',
  },
  methodGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '24px' },
  methodCard: (selected: boolean) => ({
    border: selected ? '2px solid #1a73e8' : '2px solid #e5e7eb',
    borderRadius: '12px',
    padding: '14px 12px',
    cursor: 'pointer',
    background: selected ? '#eff6ff' : '#fff',
    transition: 'all 0.2s ease',
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    boxShadow: selected ? '0 0 0 4px rgba(26,115,232,0.1)' : 'none',
  }),
  methodIcon: { fontSize: '22px', lineHeight: 1 },
  methodName: (selected: boolean) => ({ fontSize: '13px', fontWeight: selected ? 700 : 500, color: selected ? '#1a73e8' : '#374151' }),
  errorBox: {
    background: '#fef2f2',
    border: '1px solid #fecaca',
    borderRadius: '10px',
    padding: '12px 16px',
    color: '#dc2626',
    fontSize: '14px',
    marginBottom: '16px',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  btnCancel: {
    border: '2px solid #e5e7eb',
    borderRadius: '12px',
    padding: '12px 20px',
    background: '#fff',
    color: '#6b7280',
    fontWeight: 600,
    cursor: 'pointer',
    fontSize: '14px',
    transition: 'all 0.2s',
  },
  btnPay: (disabled: boolean) => ({
    flex: 1,
    border: 'none',
    borderRadius: '12px',
    padding: '14px 20px',
    background: disabled ? '#9ca3af' : 'linear-gradient(135deg, #1a73e8, #0d47a1)',
    color: '#fff',
    fontWeight: 700,
    cursor: disabled ? 'not-allowed' : 'pointer',
    fontSize: '15px',
    transition: 'all 0.2s',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
  }),
  security: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '6px',
    marginTop: '16px',
    fontSize: '12px',
    color: '#9ca3af',
  },
  processingOverlay: {
    position: 'fixed' as const,
    inset: 0,
    background: 'rgba(0,0,0,0.75)',
    display: 'flex',
    flexDirection: 'column' as const,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 2000,
    gap: '20px',
  },
  processingText: { color: '#fff', fontSize: '18px', fontWeight: 600 },
  processingSubtext: { color: '#9ca3af', fontSize: '14px' },
};

const UserCheckoutForm = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const serviceId = searchParams.get('serviceId');
  const price = searchParams.get('price');
  const serviceName = searchParams.get('serviceName') ?? 'Servicio';

  const [selectedMethod, setSelectedMethod] = useState<string>('');
  const [submitting, setSubmitting] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState('');

  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);

  useEffect(() => {
    dispatch(getPaymentMethods({}));
  }, []);

  const handleCheckout = async () => {
    if (!selectedMethod) {
      setError('Por favor selecciona un método de pago para continuar.');
      return;
    }
    setSubmitting(true);
    setError('');
    setProcessing(true);

    await new Promise(r => setTimeout(r, 2500));
    setProcessing(false);

    try {
      const res = await axios.post('/api/payments/checkout', {
        serviceId: Number(serviceId),
        paymentMethodId: Number(selectedMethod),
      });
      const data = res.data;
      navigate(
        `/payment/success?serviceName=${encodeURIComponent(data.serviceName)}&hasCourses=${data.hasCourses}&transactionId=${data.transactionId}&amount=${data.amount}&serviceId=${data.serviceId}`,
      );
    } catch {
      setError('Error al procesar el pago. Verifica tu método de pago e intenta de nuevo.');
      setSubmitting(false);
    }
  };

  const selectedMethodObj = paymentMethods.find(m => m.id?.toString() === selectedMethod);

  return (
    <>
      {processing && (
        <div style={styles.processingOverlay}>
          <div style={{ fontSize: '48px', animation: 'spin 1s linear infinite' }}>⏳</div>
          <div style={styles.processingText}>Procesando tu pago…</div>
          <div style={styles.processingSubtext}>Por favor no cierres esta ventana</div>
          <div style={{ display: 'flex', gap: '8px', marginTop: '8px' }}>
            {[0, 1, 2].map(i => (
              <div
                key={i}
                style={{
                  width: '8px',
                  height: '8px',
                  borderRadius: '50%',
                  background: '#1a73e8',
                  animation: `bounce 1.2s ease ${i * 0.2}s infinite`,
                }}
              />
            ))}
          </div>
        </div>
      )}

      <style>{`
        @keyframes fadeIn { from { opacity:0 } to { opacity:1 } }
        @keyframes slideUp { from { opacity:0; transform:translateY(40px) scale(0.95) } to { opacity:1; transform:translateY(0) scale(1) } }
        @keyframes spin { to { transform: rotate(360deg) } }
        @keyframes bounce { 0%,80%,100% { transform:scale(0) } 40% { transform:scale(1) } }
      `}</style>

      <div style={styles.overlay}>
        <div style={styles.modal}>
          <div style={styles.header}>
            <div style={styles.logo}>🏋️</div>
            <div>
              <div style={styles.title}>GymTrack Pay</div>
              <div style={styles.subtitle}>Pago seguro y encriptado</div>
            </div>
          </div>

          <div style={styles.summaryBox}>
            <div style={styles.summaryRow}>
              <span style={styles.summaryLabel}>Servicio</span>
              <span style={styles.summaryValue}>{serviceName}</span>
            </div>
            <div style={styles.summaryRow}>
              <span style={styles.summaryLabel}>Método seleccionado</span>
              <span style={styles.summaryValue}>
                {selectedMethodObj ? `${getMethodIcon(selectedMethodObj.methodName)} ${selectedMethodObj.methodName}` : '—'}
              </span>
            </div>
            <hr style={styles.divider} />
            <div style={styles.summaryRow}>
              <span style={styles.totalLabel}>Total a pagar</span>
              <span style={styles.totalValue}>${Number(price).toLocaleString('es-CO')}</span>
            </div>
          </div>

          <div style={styles.sectionTitle}>Selecciona método de pago</div>
          <div style={styles.methodGrid}>
            {paymentMethods.map(m => (
              <div
                key={m.id}
                style={styles.methodCard(selectedMethod === m.id?.toString())}
                onClick={() => {
                  setSelectedMethod(m.id?.toString());
                  setError('');
                }}
              >
                <span style={styles.methodIcon}>{getMethodIcon(m.methodName)}</span>
                <span style={styles.methodName(selectedMethod === m.id?.toString())}>{m.methodName}</span>
              </div>
            ))}
          </div>

          {error && (
            <div style={styles.errorBox}>
              <span>⚠️</span> {error}
            </div>
          )}

          <div style={{ display: 'flex', gap: '12px' }}>
            <button style={styles.btnCancel} onClick={() => navigate('/gym-service')} disabled={submitting}>
              ← Cancelar
            </button>
            <button style={styles.btnPay(submitting)} onClick={handleCheckout} disabled={submitting}>
              {submitting ? (
                <>
                  <span>⏳</span> Procesando…
                </>
              ) : (
                <>
                  <span>🔒</span> Pagar ${Number(price).toLocaleString('es-CO')}
                </>
              )}
            </button>
          </div>

          <div style={styles.security}>
            <span>🔒</span>
            <span>Transacción protegida con cifrado SSL de 256 bits</span>
          </div>
        </div>
      </div>
    </>
  );
};

const AdminPaymentForm = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);
  const userData = useAppSelector(state => state.userData.entities);
  const paymentEntity = useAppSelector(state => state.payment.entity);
  const loading = useAppSelector(state => state.payment.loading);
  const updating = useAppSelector(state => state.payment.updating);
  const updateSuccess = useAppSelector(state => state.payment.updateSuccess);

  useEffect(() => {
    if (isNew) dispatch(reset());
    else dispatch(getEntity(id));
    dispatch(getPaymentMethods({}));
    dispatch(getUserData({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) navigate('/payment');
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.amountPaid !== undefined && typeof values.amountPaid !== 'number') values.amountPaid = Number(values.amountPaid);
    values.paymentDate = convertDateTimeToServer(values.paymentDate);
    const entity = {
      ...paymentEntity,
      ...values,
      paymentMethod: paymentMethods.find(it => it.id.toString() === values.paymentMethod?.toString()),
      registeredBy: userData.find(it => it.id.toString() === values.registeredBy?.toString()),
    };
    if (isNew) dispatch(createEntity(entity));
    else dispatch(updateEntity(entity));
  };

  const defaultValues = () =>
    isNew
      ? { paymentDate: displayDefaultDateTime() }
      : {
          ...paymentEntity,
          paymentDate: convertDateTimeFromServer(paymentEntity.paymentDate),
          paymentMethod: paymentEntity?.paymentMethod?.id,
          registeredBy: paymentEntity?.registeredBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2>
            <Translate contentKey="gymtrackApp.payment.home.createOrEditLabel" />
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="payment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('gymtrackApp.payment.amountPaid')}
                id="payment-amountPaid"
                name="amountPaid"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.paymentDate')}
                id="payment-paymentDate"
                name="paymentDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{ required: { value: true, message: translate('entity.validation.required') } }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.transactionId')}
                id="payment-transactionId"
                name="transactionId"
                type="text"
                validate={{ maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) } }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.status')}
                id="payment-status"
                name="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                id="payment-paymentMethod"
                name="paymentMethod"
                type="select"
                required
                label={translate('gymtrackApp.payment.paymentMethod')}
              >
                <option value="" key="0" />
                {paymentMethods?.map(m => (
                  <option value={m.id} key={m.id}>
                    {m.methodName}
                  </option>
                ))}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required" />
              </FormText>
              <ValidatedField
                id="payment-registeredBy"
                name="registeredBy"
                type="select"
                required
                label={translate('gymtrackApp.payment.registeredBy')}
              >
                <option value="" key="0" />
                {userData?.map(u => (
                  <option value={u.id} key={u.id}>
                    {u.document}
                  </option>
                ))}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required" />
              </FormText>
              <Button tag={Link} to="/payment" color="info">
                <FontAwesomeIcon icon="arrow-left" /> <Translate contentKey="entity.action.back" />
              </Button>
              &nbsp;
              <Button color="primary" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" /> <Translate contentKey="entity.action.save" />
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export const PaymentUpdate = () => {
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));
  return isAdmin ? <AdminPaymentForm /> : <UserCheckoutForm />;
};

export default PaymentUpdate;
