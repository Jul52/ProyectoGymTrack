import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import axios from 'axios';

const styles = {
  page: {
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '24px',
    fontFamily: 'system-ui, sans-serif',
  },
  card: {
    background: '#fff',
    borderRadius: '24px',
    boxShadow: '0 20px 60px rgba(0,0,0,0.1)',
    padding: '48px 40px',
    maxWidth: '480px',
    width: '100%',
    textAlign: 'center' as const,
    animation: 'fadeInUp 0.5s cubic-bezier(0.34,1.2,0.64,1)',
  },
  iconWrap: {
    width: '88px',
    height: '88px',
    background: 'linear-gradient(135deg, #22c55e, #16a34a)',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    margin: '0 auto 24px',
    fontSize: '40px',
    animation: 'checkPop 0.6s cubic-bezier(0.34,1.56,0.64,1) 0.2s both',
    boxShadow: '0 8px 24px rgba(34,197,94,0.3)',
  },
  title: { fontSize: '26px', fontWeight: 800, color: '#14532d', marginBottom: '8px' },
  subtitle: { fontSize: '15px', color: '#6b7280', marginBottom: '32px', lineHeight: 1.6 },
  infoBox: {
    background: '#f0fdf4',
    border: '1px solid #bbf7d0',
    borderRadius: '14px',
    padding: '20px',
    marginBottom: '28px',
    textAlign: 'left' as const,
  },
  infoRow: { display: 'flex', justifyContent: 'space-between', padding: '6px 0', fontSize: '14px' },
  infoLabel: { color: '#6b7280' },
  infoValue: { fontWeight: 700, color: '#1a1a2e' },
  btnPrimary: {
    display: 'block',
    width: '100%',
    padding: '14px',
    background: 'linear-gradient(135deg, #1a73e8, #0d47a1)',
    color: '#fff',
    border: 'none',
    borderRadius: '12px',
    fontWeight: 700,
    fontSize: '15px',
    cursor: 'pointer',
    marginBottom: '12px',
    textDecoration: 'none',
    textAlign: 'center' as const,
  },
  btnSecondary: {
    display: 'block',
    width: '100%',
    padding: '13px',
    background: '#fff',
    color: '#374151',
    border: '2px solid #e5e7eb',
    borderRadius: '12px',
    fontWeight: 600,
    fontSize: '15px',
    cursor: 'pointer',
    textDecoration: 'none',
    textAlign: 'center' as const,
  },
  security: { marginTop: '20px', fontSize: '12px', color: '#9ca3af' },
};

const PaymentSuccess = () => {
  const [searchParams] = useSearchParams();
  const [serviceInfo, setServiceInfo] = useState<any>(null);
  const hasCourses = searchParams.get('hasCourses') === 'true';
  const serviceId = searchParams.get('serviceId');
  const serviceName = searchParams.get('serviceName') ?? 'Servicio';
  const transactionId = searchParams.get('transactionId') ?? '—';
  const amount = searchParams.get('amount');

  useEffect(() => {
    if (serviceId) {
      axios
        .get(`/api/gym-services/${serviceId}`)
        .then(r => setServiceInfo(r.data))
        .catch(() => {});
    }
  }, [serviceId]);

  const hasCoursesInService =
    hasCourses || serviceInfo?.courses?.length > 0 || (serviceInfo?.courseAccessType && serviceInfo.courseAccessType !== 'NONE');

  return (
    <>
      <style>{`
        @keyframes fadeInUp { from { opacity:0; transform:translateY(30px) } to { opacity:1; transform:translateY(0) } }
        @keyframes checkPop { 0% { transform:scale(0) } 70% { transform:scale(1.2) } 100% { transform:scale(1) } }
      `}</style>
      <div style={styles.page}>
        <div style={styles.card}>
          <div style={styles.iconWrap}>✅</div>
          <div style={styles.title}>¡Pago exitoso!</div>
          <div style={styles.subtitle}>Tu pago fue procesado correctamente. Ya puedes acceder a los beneficios de tu servicio.</div>

          <div style={styles.infoBox}>
            <div style={styles.infoRow}>
              <span style={styles.infoLabel}>Servicio</span>
              <span style={styles.infoValue}>{serviceName}</span>
            </div>
            {amount && (
              <div style={styles.infoRow}>
                <span style={styles.infoLabel}>Monto pagado</span>
                <span style={styles.infoValue}>${Number(amount).toLocaleString('es-CO')}</span>
              </div>
            )}
            <div style={styles.infoRow}>
              <span style={styles.infoLabel}>ID Transacción</span>
              <span style={{ ...styles.infoValue, fontFamily: 'monospace', fontSize: '12px' }}>{transactionId}</span>
            </div>
            <div style={styles.infoRow}>
              <span style={styles.infoLabel}>Estado</span>
              <span style={{ ...styles.infoValue, color: '#16a34a' }}>✅ Completado</span>
            </div>
          </div>

          {hasCoursesInService && (
            <Link to="/reservation/new" style={styles.btnPrimary}>
              📅 Reservar un curso ahora
            </Link>
          )}

          <Link to="/gym-service" style={hasCoursesInService ? styles.btnSecondary : styles.btnPrimary}>
            🏋️ Ver mis servicios
          </Link>

          <Link to="/invoice" style={styles.btnSecondary}>
            🧾 Ver mis facturas
          </Link>

          <div style={styles.security}>🔒 Transacción segura · GymTrack Pay</div>
        </div>
      </div>
    </>
  );
};

export default PaymentSuccess;
