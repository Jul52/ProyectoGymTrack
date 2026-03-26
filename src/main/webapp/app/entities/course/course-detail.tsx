import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity } from './course.reducer';

export const CourseDetail = () => {
  const dispatch = useAppDispatch();
  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const c = useAppSelector(state => state.course.entity);
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));

  const zonesLabel = c.zones?.length > 0 ? c.zones.map(z => z.name).join(', ') : '—';

  const formatDate = (val: string) =>
    val ? new Date(val).toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' }) : '—';

  return (
    <>
      <style>{`
        @keyframes fadeInUp { from { opacity:0; transform:translateY(20px) } to { opacity:1; transform:translateY(0) } }
        .crs-card { animation: fadeInUp 0.4s ease; }
        .crs-row:not(:last-child) { border-bottom: 1px dashed #e5e7eb; }
      `}</style>

      <Row className="justify-content-center" style={{ padding: '32px 16px' }}>
        <Col md="7" lg="6">
          <div
            className="crs-card"
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
                background: 'linear-gradient(135deg, #0369a1 0%, #075985 100%)',
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
                📚
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
                Detalle del Curso
              </div>
              <div style={{ color: '#fff', fontSize: '26px', fontWeight: 800 }}>{c.courseName ?? '—'}</div>
              <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: '13px', marginTop: '4px' }}>{zonesLabel}</div>
            </div>

            {/* Badge estado */}
            <div style={{ display: 'flex', justifyContent: 'center', marginTop: '-18px', position: 'relative', zIndex: 1 }}>
              <div
                style={{
                  background: c.status ? '#dcfce7' : '#fee2e2',
                  color: c.status ? '#15803d' : '#dc2626',
                  border: `2px solid ${c.status ? '#15803d30' : '#dc262630'}`,
                  borderRadius: '999px',
                  padding: '6px 18px',
                  fontSize: '13px',
                  fontWeight: 700,
                  boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                }}
              >
                {c.status ? '✅ Activo' : '❌ Inactivo'}
              </div>
            </div>

            {/* Detalles */}
            <div style={{ padding: '24px 28px 8px' }}>
              {[
                { label: 'ID', value: `#${c.id}`, icon: '🔢' },
                { label: 'Nombre', value: c.courseName ?? '—', icon: '📚' },
                { label: 'Estado', value: c.status ? '✅ Activo' : '❌ Inactivo', icon: '🔖' },
                { label: 'Fecha de Inicio', value: formatDate(c.startDate), icon: '📅' },
                { label: 'Fecha de Fin', value: formatDate(c.endDate), icon: '📅' },
                { label: 'Capacidad', value: c.capacity ? `${c.capacity} personas` : '—', icon: '👥' },
                { label: 'Zona', value: zonesLabel, icon: '📍' },
                { label: 'Entrenador', value: c.trainer?.id ? `#${c.trainer.id}` : '—', icon: '🏋️' },
              ].map((item, idx) => (
                <div
                  key={idx}
                  className="crs-row"
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
                  <span style={{ fontSize: '13px', fontWeight: 700, color: '#1a1a2e', textAlign: 'right', maxWidth: '60%' }}>
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

            {/* Botones */}
            <div style={{ padding: '20px 28px', display: 'flex', gap: '12px', borderTop: '1px solid #f3f4f6' }}>
              <Button tag={Link} to="/course" color="light" style={{ flex: 1, borderRadius: '12px', fontWeight: 600 }}>
                <FontAwesomeIcon icon="arrow-left" /> Volver
              </Button>
              {isAdmin && (
                <Button
                  tag={Link}
                  to={`/course/${c.id}/edit`}
                  color="primary"
                  style={{
                    flex: 1,
                    borderRadius: '12px',
                    fontWeight: 600,
                    background: 'linear-gradient(135deg, #0369a1, #075985)',
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

export default CourseDetail;
