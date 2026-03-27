import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { getEntity } from './invoice.reducer';

const METHOD_ICONS: Record<string, string> = {
  nequi: '📱',
  daviplata: '💜',
  efectivo: '💵',
  'tarjeta de crédito': '💳',
  'tarjeta de debito': '💳',
  'tarjeta de débito': '💳',
  pse: '🏦',
  transferencia: '🔄',
  'transferencia bancaria': '🔄',
};

const getMethodIcon = (name: string) => METHOD_ICONS[name?.toLowerCase()] ?? '💳';

const formatCurrency = (value: any) => {
  if (value === undefined || value === null) return '—';
  const num = typeof value === 'string' ? parseFloat(value) : value;
  return isNaN(num) ? '—' : `$${num.toLocaleString('es-CO', { minimumFractionDigits: 0 })}`;
};

export const InvoiceDetail = () => {
  const dispatch = useAppDispatch();
  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const inv = useAppSelector(state => state.invoice.entity);
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));
  const methodName = inv.paymentMethod?.methodName ?? '';

  const expiryDate = inv.createdDate ? new Date(new Date(inv.createdDate).getTime() + 30 * 24 * 60 * 60 * 1000) : null;

  const isExpired = expiryDate ? expiryDate < new Date() : false;

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
        .invoice-card { animation: fadeInUp 0.5s cubic-bezier(0.34,1.2,0.64,1); }
        .invoice-icon { animation: checkPop 0.6s cubic-bezier(0.34,1.56,0.64,1) 0.2s both; }
        .detail-row:not(:last-child) { border-bottom: 1px dashed #e5e7eb; }
        @media print {
          .no-print { display: none !important; }
          .invoice-card { box-shadow: none !important; }
        }
      `}</style>

      <Row className="justify-content-center" style={{ padding: '32px 16px' }}>
        <Col md="7" lg="6">
          <div
            className="invoice-card"
            style={{
              background: '#fff',
              borderRadius: '24px',
              boxShadow: '0 8px 40px rgba(0,0,0,0.12)',
              overflow: 'hidden',
            }}
          >
            {/* Header */}
            <div
              style={{
                background: 'linear-gradient(135deg, #0f766e 0%, #0d5c55 100%)',
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
                className="invoice-icon"
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
                🧾
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
                Factura de Servicio
              </div>
              <div style={{ color: '#fff', fontSize: '36px', fontWeight: 800, lineHeight: 1, marginBottom: '4px' }}>
                {formatCurrency(inv.total)}
              </div>
              <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: '13px' }}>
                {inv.createdDate
                  ? new Date(inv.createdDate).toLocaleDateString('es-CO', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                    })
                  : '—'}
              </div>
            </div>

            {/* Badge de estado del servicio */}
            <div style={{ display: 'flex', justifyContent: 'center', marginTop: '-18px', position: 'relative', zIndex: 1 }}>
              <div
                style={{
                  background: isExpired ? '#fee2e2' : '#dcfce7',
                  color: isExpired ? '#dc2626' : '#15803d',
                  border: `2px solid ${isExpired ? '#dc262630' : '#15803d30'}`,
                  borderRadius: '999px',
                  padding: '6px 18px',
                  fontSize: '13px',
                  fontWeight: 700,
                  boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                }}
              >
                {isExpired ? '❌ Servicio vencido' : '✅ Servicio activo'}
              </div>
            </div>

            {/* Detalles */}
            <div style={{ padding: '24px 28px 8px' }}>
              {[
                { label: 'N° Factura', value: `#${inv.id}`, icon: '🔢' },
                { label: 'Servicio', value: inv.service?.serviceName ?? '—', icon: '🏋️' },
                { label: 'Cliente', value: inv.userData?.documentNumber ?? '—', icon: '👤' },
                { label: 'Método de pago', value: methodName ? `${getMethodIcon(methodName)} ${methodName}` : '—', icon: '💳' },
                { label: 'Pago ID', value: inv.payment?.id ? `#${inv.payment.id}` : '—', icon: '🧾' },
                {
                  label: 'Fecha de emisión',
                  value: inv.createdDate
                    ? new Date(inv.createdDate).toLocaleDateString('es-CO', { year: 'numeric', month: 'long', day: 'numeric' })
                    : '—',
                  icon: '📅',
                },
                {
                  label: 'Válido hasta',
                  value: expiryDate ? expiryDate.toLocaleDateString('es-CO', { year: 'numeric', month: 'long', day: 'numeric' }) : '—',
                  icon: '⏳',
                },
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
                  <span style={{ fontSize: '13px', fontWeight: 700, color: '#1a1a2e', textAlign: 'right', maxWidth: '55%' }}>
                    {item.value}
                  </span>
                </div>
              ))}
            </div>

            {/* Línea punteada */}
            <div style={{ padding: '0 20px', margin: '8px 0' }}>
              <div style={{ borderTop: '2px dashed #e5e7eb', position: 'relative' }}>
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

            {/* Total */}
            <div style={{ padding: '16px 28px 28px' }}>
              <div
                style={{
                  background: 'linear-gradient(135deg, #f0fdf4, #dcfce7)',
                  borderRadius: '14px',
                  padding: '16px 20px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <span style={{ fontSize: '15px', fontWeight: 600, color: '#374151' }}>💰 Total Facturado</span>
                <span style={{ fontSize: '24px', fontWeight: 800, color: '#0f766e' }}>{formatCurrency(inv.total)}</span>
              </div>
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
                🔒 Factura oficial · GymTrack
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
              <Button tag={Link} to="/invoice" color="light" style={{ flex: 1, borderRadius: '12px', fontWeight: 600 }}>
                <FontAwesomeIcon icon="arrow-left" /> Volver
              </Button>
              <Button
                onClick={() => window.print()}
                color="light"
                style={{ borderRadius: '12px', fontWeight: 600 }}
                title="Imprimir factura"
              >
                🖨️
              </Button>
              {isAdmin && (
                <Button
                  tag={Link}
                  to={`/invoice/${inv.id}/edit`}
                  color="primary"
                  style={{
                    flex: 1,
                    borderRadius: '12px',
                    fontWeight: 600,
                    background: 'linear-gradient(135deg, #0f766e, #0d5c55)',
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

export default InvoiceDetail;
