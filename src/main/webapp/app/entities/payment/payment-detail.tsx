import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity } from './payment.reducer';
import { AUTHORITIES } from 'app/config/constants';

// ─── Utilidades ───────────────────────────────────────────────────────────────
const STATUS_CONFIG: Record<string, { label: string; color: string; bg: string; icon: string }> = {
  completed: { label: 'Completado', color: '#15803d', bg: '#dcfce7', icon: '✅' },
  COMPLETED: { label: 'Completado', color: '#15803d', bg: '#dcfce7', icon: '✅' },
  pending: { label: 'Pendiente', color: '#b45309', bg: '#fef3c7', icon: '⏳' },
  PENDING: { label: 'Pendiente', color: '#b45309', bg: '#fef3c7', icon: '⏳' },
  failed: { label: 'Fallido', color: '#dc2626', bg: '#fee2e2', icon: '❌' },
  FAILED: { label: 'Fallido', color: '#dc2626', bg: '#fee2e2', icon: '❌' },
  cancelled: { label: 'Cancelado', color: '#6b7280', bg: '#f3f4f6', icon: '🚫' },
  CANCELLED: { label: 'Cancelado', color: '#6b7280', bg: '#f3f4f6', icon: '🚫' },
};

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

const formatCurrency = (value: number | string | undefined) => {
  if (value === undefined || value === null) return '—';
  const num = typeof value === 'string' ? parseFloat(value) : value;
  return isNaN(num) ? '—' : `$${num.toLocaleString('es-CO', { minimumFractionDigits: 0 })}`;
};

// ─── Componente principal ─────────────────────────────────────────────────────
export const PaymentDetail = () => {
  const dispatch = useAppDispatch();
  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const p = useAppSelector(state => state.payment.entity);
  const status = STATUS_CONFIG[p.status ?? ''] ?? { label: p.status, color: '#6b7280', bg: '#f3f4f6', icon: '💳' };
  const methodName = p.paymentMethod?.methodName ?? '';
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));

  return (
    <>
      <style>{`
        @keyframes fadeInUp {
          from { opacity: 0; transform: translateY(24px); }
          to   { opacity: 1; transform: translateY(0); }
        }
        @keyframes checkPop {
          0%   { transform: scale(0) rotate(-10deg); }
          70%  { transform: scale(1.15) rotate(3deg); }
          100% { transform: scale(1) rotate(0deg); }
        }
        .receipt-card {
          animation: fadeInUp 0.5s cubic-bezier(0.34,1.2,0.64,1);
        }
        .receipt-icon {
          animation: checkPop 0.6s cubic-bezier(0.34,1.56,0.64,1) 0.2s both;
        }
        .detail-row:not(:last-child) {
          border-bottom: 1px dashed #e5e7eb;
        }
        @media print {
          .no-print { display: none !important; }
          .receipt-card { box-shadow: none !important; }
        }
      `}</style>

      <Row className="justify-content-center" style={{ padding: '32px 16px' }}>
        <Col md="7" lg="6">
          {/* Recibo */}
          <div
            className="receipt-card"
            style={{
              background: '#fff',
              borderRadius: '24px',
              boxShadow: '0 8px 40px rgba(0,0,0,0.12)',
              overflow: 'hidden',
            }}
          >
            {/* Header con gradiente */}
            <div
              style={{
                background: 'linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%)',
                padding: '32px 28px 28px',
                textAlign: 'center',
                position: 'relative',
              }}
            >
              <div
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  backgroundImage:
                    'radial-gradient(circle at 20% 50%, rgba(255,255,255,0.08) 0%, transparent 50%), radial-gradient(circle at 80% 20%, rgba(255,255,255,0.05) 0%, transparent 40%)',
                }}
              />

              <div
                className="receipt-icon"
                style={{
                  width: '72px',
                  height: '72px',
                  background: 'rgba(255,255,255,0.2)',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '32px',
                  margin: '0 auto 16px',
                  position: 'relative',
                }}
              >
                {status.icon}
              </div>
              <div
                style={{
                  color: 'rgba(255,255,255,0.8)',
                  fontSize: '13px',
                  fontWeight: 500,
                  letterSpacing: '1px',
                  textTransform: 'uppercase',
                  marginBottom: '8px',
                }}
              >
                Comprobante de Pago
              </div>
              <div style={{ color: '#fff', fontSize: '36px', fontWeight: 800, lineHeight: 1, marginBottom: '4px' }}>
                {formatCurrency(p.amountPaid)}
              </div>
              <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: '13px' }}>
                {p.paymentDate
                  ? new Date(p.paymentDate).toLocaleDateString('es-CO', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit',
                    })
                  : '—'}
              </div>
            </div>

            {/* Badge de estado */}
            <div style={{ display: 'flex', justifyContent: 'center', marginTop: '-18px', position: 'relative', zIndex: 1 }}>
              <div
                style={{
                  background: status.bg,
                  color: status.color,
                  border: `2px solid ${status.color}30`,
                  borderRadius: '999px',
                  padding: '6px 18px',
                  fontSize: '13px',
                  fontWeight: 700,
                  boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                }}
              >
                {status.icon} {status.label}
              </div>
            </div>

            {/* Detalles */}
            <div style={{ padding: '24px 28px 8px' }}>
              {[
                { label: 'ID de Pago', value: `#${p.id}`, icon: '🔢' },
                { label: 'ID de Transacción', value: p.transactionId || '—', icon: '🧾', mono: true },
                {
                  label: 'Método de Pago',
                  value: methodName ? `${getMethodIcon(methodName)} ${methodName}` : '—',
                  icon: '💳',
                },
                { label: 'Servicio', value: p.serviceName ?? '—', icon: '🏋️' },
                { label: 'Registrado por', value: p.registeredBy?.documentNumber || '—', icon: '👤' },
              ].map((item, idx) => (
                <div
                  key={idx}
                  className="detail-row"
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '14px 0',
                    gap: '12px',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flex: 1 }}>
                    <span style={{ fontSize: '16px' }}>{item.icon}</span>
                    <span style={{ fontSize: '13px', color: '#6b7280', fontWeight: 500 }}>{item.label}</span>
                  </div>
                  <span
                    style={{
                      fontSize: '13px',
                      fontWeight: 700,
                      color: '#1a1a2e',
                      fontFamily: item.mono ? 'monospace' : 'inherit',
                      textAlign: 'right',
                      maxWidth: '55%',
                      wordBreak: 'break-all',
                    }}
                  >
                    {item.value}
                  </span>
                </div>
              ))}
            </div>

            {/* Línea punteada estilo recibo */}
            <div style={{ padding: '0 20px', margin: '8px 0' }}>
              <div
                style={{
                  borderTop: '2px dashed #e5e7eb',
                  position: 'relative',
                }}
              >
                <div
                  style={{
                    position: 'absolute',
                    left: '-28px',
                    top: '-12px',
                    width: '24px',
                    height: '24px',
                    background: '#f9fafb',
                    borderRadius: '50%',
                    border: '2px dashed #e5e7eb',
                  }}
                />
                <div
                  style={{
                    position: 'absolute',
                    right: '-28px',
                    top: '-12px',
                    width: '24px',
                    height: '24px',
                    background: '#f9fafb',
                    borderRadius: '50%',
                    border: '2px dashed #e5e7eb',
                  }}
                />
              </div>
            </div>

            {/* Total final */}
            <div style={{ padding: '16px 28px 28px' }}>
              <div
                style={{
                  background: 'linear-gradient(135deg, #f0f7ff, #e8f4fd)',
                  borderRadius: '14px',
                  padding: '16px 20px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <span style={{ fontSize: '15px', fontWeight: 600, color: '#374151' }}>💰 Monto Total</span>
                <span style={{ fontSize: '24px', fontWeight: 800, color: '#1a73e8' }}>{formatCurrency(p.amountPaid)}</span>
              </div>

              {/* Seguridad */}
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '6px',
                  marginTop: '16px',
                  fontSize: '12px',
                  color: '#9ca3af',
                }}
              >
                🔒 Transacción segura · GymTrack Pay
              </div>
            </div>

            {/* Botones */}
            <div
              className="no-print"
              style={{
                borderTop: '1px solid #f3f4f6',
                padding: '20px 28px',
                display: 'flex',
                gap: '12px',
              }}
            >
              <Button tag={Link} to="/payment" color="light" style={{ flex: 1, borderRadius: '12px', fontWeight: 600 }}>
                <FontAwesomeIcon icon="arrow-left" /> Volver
              </Button>
              <Button
                onClick={() => window.print()}
                color="light"
                style={{ borderRadius: '12px', fontWeight: 600 }}
                title="Imprimir recibo"
              >
                🖨️
              </Button>
              {isAdmin && (
                <Button
                  tag={Link}
                  to={`/payment/${p.id}/edit`}
                  color="primary"
                  style={{
                    flex: 1,
                    borderRadius: '12px',
                    fontWeight: 600,
                    background: 'linear-gradient(135deg, #1a73e8, #0d47a1)',
                    border: 'none',
                  }}
                >
                  <FontAwesomeIcon icon="pencil-alt" /> Editar
                </Button>
              )}
            </div>
          </div>
        </Col>
      </Row>
    </>
  );
};

export default PaymentDetail;
