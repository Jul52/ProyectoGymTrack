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
  'tarjeta de crédito': '💳',
  'tarjeta de debito': '💳',
  'tarjeta de débito': '💳',
  pse: '🏦',
  transferencia: '🔄',
  'transferencia bancaria': '🔄',
};

const getMethodIcon = (name: string) => METHOD_ICONS[name?.toLowerCase()] ?? '💳';

const isCardMethod = (name: string) => ['tarjeta de crédito', 'tarjeta de débito', 'tarjeta de debito'].includes(name?.toLowerCase());
const isPhoneMethod = (name: string) => ['nequi', 'daviplata'].includes(name?.toLowerCase());

const styles = {
  overlay: {
    position: 'fixed' as const,
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'rgba(0,0,0,0.6)',
    backdropFilter: 'blur(4px)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1050,
    overflowY: 'auto' as const,
    paddingTop: '20px',
    paddingBottom: '20px',
  },
  modal: {
    background: '#fff',
    borderRadius: '20px',
    padding: '24px',
    width: '100%',
    maxWidth: '460px',
    boxShadow: '0 25px 60px rgba(0,0,0,0.3)',
    animation: 'slideUp 0.4s cubic-bezier(0.34,1.56,0.64,1)',
  },
  header: { display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px' },
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
    padding: '12px 16px',
    marginBottom: '16px',
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
  methodGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginBottom: '16px' },
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
  input: {
    width: '100%',
    padding: '12px 14px',
    border: '2px solid #e5e7eb',
    borderRadius: '10px',
    fontSize: '14px',
    outline: 'none',
    transition: 'border-color 0.2s',
    marginBottom: '14px',
    fontFamily: 'inherit',
  },
  inputLabel: { fontSize: '13px', fontWeight: 600, color: '#374151', marginBottom: '6px', display: 'block' },
  inputRow: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' },
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
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
  }),
  btnBack: {
    border: '2px solid #e5e7eb',
    borderRadius: '12px',
    padding: '12px 20px',
    background: '#fff',
    color: '#6b7280',
    fontWeight: 600,
    cursor: 'pointer',
    fontSize: '14px',
  },
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

  const [step, setStep] = useState<1 | 2>(1);
  const [selectedMethod, setSelectedMethod] = useState<string>('');
  const [submitting, setSubmitting] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState('');

  // Campos de tarjeta
  const [cardNumber, setCardNumber] = useState('');
  const [cardName, setCardName] = useState('');
  const [cardExpiry, setCardExpiry] = useState('');
  const [cardCvv, setCardCvv] = useState('');

  // Campos de billetera
  const [phoneNumber, setPhoneNumber] = useState('');

  // Transferencia
  const [bankRef, setBankRef] = useState('');

  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);
  const activePaymentMethods = paymentMethods.filter((m: any) => m.active !== false && m.methodCode !== 'CASH');

  useEffect(() => {
    dispatch(getPaymentMethods({}));
  }, []);

  const selectedMethodObj = paymentMethods.find(m => m.id?.toString() === selectedMethod);
  const methodName = selectedMethodObj?.methodName ?? '';

  const formatCardNumber = (val: string) => {
    return val
      .replace(/\D/g, '')
      .substring(0, 16)
      .replace(/(.{4})/g, '$1 ')
      .trim();
  };

  const formatExpiry = (val: string) => {
    const clean = val.replace(/\D/g, '').substring(0, 4);
    if (clean.length >= 3) return clean.substring(0, 2) + '/' + clean.substring(2);
    return clean;
  };

  const goToStep2 = () => {
    if (!selectedMethod) {
      setError('Por favor selecciona un método de pago para continuar.');
      return;
    }
    setError('');
    setStep(2);
  };

  const handleCheckout = async () => {
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
      setError('Error al procesar el pago. Verifica tus datos e intenta de nuevo.');
      setSubmitting(false);
    }
  };

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
        .pay-input:focus { border-color: #1a73e8 !important; box-shadow: 0 0 0 3px rgba(26,115,232,0.15); }
      `}</style>

      <div style={styles.overlay}>
        <div style={styles.modal}>
          {/* Header */}
          <div style={styles.header}>
            <div style={styles.logo}>🏋️</div>
            <div>
              <div style={styles.title}>GymTrack Pay</div>
              <div style={styles.subtitle}>{step === 1 ? 'Pago seguro y encriptado' : `Paso 2 de 2 — ${methodName}`}</div>
            </div>
          </div>

          {/* Resumen */}
          <div style={styles.summaryBox}>
            <div style={styles.summaryRow}>
              <span style={styles.summaryLabel}>Servicio</span>
              <span style={styles.summaryValue}>{serviceName}</span>
            </div>
            {step === 2 && (
              <div style={styles.summaryRow}>
                <span style={styles.summaryLabel}>Método</span>
                <span style={styles.summaryValue}>
                  {getMethodIcon(methodName)} {methodName}
                </span>
              </div>
            )}
            <hr style={styles.divider} />
            <div style={styles.summaryRow}>
              <span style={styles.totalLabel}>Total a pagar</span>
              <span style={styles.totalValue}>${Number(price).toLocaleString('es-CO')}</span>
            </div>
          </div>

          {/* PASO 1 — Selección de método */}
          {step === 1 && (
            <>
              <div style={styles.sectionTitle}>Selecciona método de pago</div>
              <div style={styles.methodGrid}>
                {activePaymentMethods.map((m: any) => (
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
                <button style={styles.btnCancel} onClick={() => navigate('/gym-service')}>
                  ← Cancelar
                </button>
                <button style={styles.btnPay(!selectedMethod)} onClick={goToStep2} disabled={!selectedMethod}>
                  Continuar →
                </button>
              </div>
            </>
          )}

          {/* PASO 2 — Datos del método seleccionado */}
          {step === 2 && (
            <>
              {isCardMethod(methodName) && (
                <div>
                  <div style={styles.sectionTitle}>Datos de la tarjeta</div>
                  <label style={styles.inputLabel}>Número de tarjeta</label>
                  <input
                    className="pay-input"
                    style={styles.input}
                    placeholder="1234 5678 9012 3456"
                    value={cardNumber}
                    onChange={e => setCardNumber(formatCardNumber(e.target.value))}
                    maxLength={19}
                  />
                  <label style={styles.inputLabel}>Nombre en la tarjeta</label>
                  <input
                    className="pay-input"
                    style={styles.input}
                    placeholder="JUAN PEREZ"
                    value={cardName}
                    onChange={e => setCardName(e.target.value.toUpperCase())}
                  />
                  <div style={styles.inputRow}>
                    <div>
                      <label style={styles.inputLabel}>Fecha de vencimiento</label>
                      <input
                        className="pay-input"
                        style={styles.input}
                        placeholder="MM/AA"
                        value={cardExpiry}
                        onChange={e => setCardExpiry(formatExpiry(e.target.value))}
                        maxLength={5}
                      />
                    </div>
                    <div>
                      <label style={styles.inputLabel}>CVV</label>
                      <input
                        className="pay-input"
                        style={{ ...styles.input, letterSpacing: '4px' }}
                        placeholder="•••"
                        type="password"
                        value={cardCvv}
                        onChange={e => setCardCvv(e.target.value.replace(/\D/g, '').substring(0, 4))}
                        maxLength={4}
                      />
                    </div>
                  </div>
                </div>
              )}

              {isPhoneMethod(methodName) && (
                <div>
                  <div style={styles.sectionTitle}>Datos de {methodName}</div>
                  <label style={styles.inputLabel}>Número de celular registrado</label>
                  <input
                    className="pay-input"
                    style={styles.input}
                    placeholder="300 000 0000"
                    value={phoneNumber}
                    onChange={e => setPhoneNumber(e.target.value.replace(/\D/g, '').substring(0, 10))}
                    maxLength={10}
                  />
                  <p style={{ fontSize: '13px', color: '#6b7280', marginTop: '-8px' }}>
                    Se enviará una notificación push a tu app de {methodName} para confirmar el pago.
                  </p>
                </div>
              )}

              {!isCardMethod(methodName) && !isPhoneMethod(methodName) && (
                <div>
                  <div style={styles.sectionTitle}>Datos de transferencia</div>
                  <label style={styles.inputLabel}>Número de referencia o comprobante</label>
                  <input
                    className="pay-input"
                    style={styles.input}
                    placeholder="Ej: 202502230001"
                    value={bankRef}
                    onChange={e => setBankRef(e.target.value)}
                  />
                  <p style={{ fontSize: '13px', color: '#6b7280', marginTop: '-8px' }}>
                    Ingresa el número de referencia de tu transferencia para validar el pago.
                  </p>
                </div>
              )}

              {error && (
                <div style={styles.errorBox}>
                  <span>⚠️</span> {error}
                </div>
              )}

              <div style={{ display: 'flex', gap: '12px' }}>
                <button
                  style={styles.btnBack}
                  onClick={() => {
                    setStep(1);
                    setError('');
                  }}
                  disabled={submitting}
                >
                  ← Volver
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
            </>
          )}

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
                    {u.documentNumber}
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
