import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { getEntity } from './machine-incidents.reducer';

export const MachineIncidentsDetail = () => {
  const dispatch = useAppDispatch();
  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const m = useAppSelector(state => state.machineIncidents.entity);
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));

  // Solo mostrar imagen si fue subida por el usuario:
  // - existe m.image y m.imageContentType
  // - el contentType es una imagen real (image/jpeg, image/png, etc.)
  // - el base64 tiene contenido suficiente (más de 100 caracteres)
  const hasValidImage = m.image && m.imageContentType && m.imageContentType.startsWith('image/') && m.image.length > 100;

  return (
    <>
      <style>{`
        @keyframes fadeInUp { from { opacity:0; transform:translateY(20px) } to { opacity:1; transform:translateY(0) } }
        .inc-card { animation: fadeInUp 0.4s ease; }
        .inc-row:not(:last-child) { border-bottom: 1px dashed #e5e7eb; }
      `}</style>

      <Row className="justify-content-center" style={{ padding: '32px 16px' }}>
        <Col md="7" lg="6">
          <div
            className="inc-card"
            style={{
              background: '#fff',
              borderRadius: '24px',
              boxShadow: '0 8px 40px rgba(0,0,0,0.1)',
              overflow: 'hidden',
            }}
          >
            {/* Header */}
            <div
              style={{
                background: 'linear-gradient(135deg, #dc2626 0%, #991b1b 100%)',
                padding: '32px 28px',
                textAlign: 'center',
              }}
            >
              <div
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
                }}
              >
                ⚠️
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
                Incidente de Máquina
              </div>
              <div style={{ color: '#fff', fontSize: '22px', fontWeight: 800 }}>{m.machine?.description ?? '—'}</div>
              <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: '14px', marginTop: '4px' }}>{m.incident?.incidentType ?? '—'}</div>
            </div>

            {/* Badge */}
            <div style={{ display: 'flex', justifyContent: 'center', marginTop: '-18px', position: 'relative', zIndex: 1 }}>
              <div
                style={{
                  background: '#fef3c7',
                  color: '#b45309',
                  border: '2px solid #b4530930',
                  borderRadius: '999px',
                  padding: '6px 18px',
                  fontSize: '13px',
                  fontWeight: 700,
                  boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                }}
              >
                🔧 Requiere atención
              </div>
            </div>

            {/* Detalles */}
            <div style={{ padding: '24px 28px 8px' }}>
              {[
                { label: 'N° Incidente', value: `#${m.id}`, icon: '🔢' },
                { label: 'Máquina', value: m.machine?.description ?? '—', icon: '🏋️' },
                { label: 'Tipo de incidente', value: m.incident?.incidentType ?? '—', icon: '⚠️' },
                { label: 'Descripción', value: m.description ?? '—', icon: '📝' },
                { label: 'Video', value: m.video ?? '—', icon: '🎥' },
              ].map((item, idx) => (
                <div
                  key={idx}
                  className="inc-row"
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '14px 0',
                    gap: '12px',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ fontSize: '16px' }}>{item.icon}</span>
                    <span style={{ fontSize: '13px', color: '#6b7280', fontWeight: 500 }}>{item.label}</span>
                  </div>
                  <span
                    style={{
                      fontSize: '13px',
                      fontWeight: 700,
                      color: '#1a1a2e',
                      textAlign: 'right',
                      maxWidth: '55%',
                      wordBreak: 'break-word',
                    }}
                  >
                    {item.value}
                  </span>
                </div>
              ))}
            </div>

            {/* Imagen — solo se muestra si el usuario subió una foto real */}
            {hasValidImage && (
              <div style={{ padding: '0 28px 20px' }}>
                <div style={{ fontSize: '13px', color: '#6b7280', fontWeight: 500, marginBottom: '10px' }}>📷 Evidencia fotográfica</div>
                <img
                  src={`data:${m.imageContentType};base64,${m.image}`}
                  alt="Evidencia"
                  style={{ width: '100%', borderRadius: '14px', cursor: 'pointer', boxShadow: '0 4px 16px rgba(0,0,0,0.12)' }}
                  onClick={() => openFile(m.imageContentType, m.image)}
                />
              </div>
            )}

            {/* Línea punteada */}
            <div style={{ padding: '0 20px', margin: '4px 0' }}>
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

            {/* Botones */}
            <div style={{ padding: '20px 28px', display: 'flex', gap: '12px', borderTop: '1px solid #f3f4f6' }}>
              <Button tag={Link} to="/machine-incidents" color="light" style={{ flex: 1, borderRadius: '12px', fontWeight: 600 }}>
                <FontAwesomeIcon icon="arrow-left" /> Volver
              </Button>
              {isAdmin && (
                <Button
                  tag={Link}
                  to={`/machine-incidents/${m.id}/edit`}
                  color="primary"
                  style={{
                    flex: 1,
                    borderRadius: '12px',
                    fontWeight: 600,
                    background: 'linear-gradient(135deg, #dc2626, #991b1b)',
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

export default MachineIncidentsDetail;
