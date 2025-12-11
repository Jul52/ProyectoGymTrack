import React, { useState, useEffect, useMemo } from 'react';

// --- SIMULACIÓN DE DATOS Y LÓGICA DE JHIPSTER ---

const mockCourseList = [
  {
    id: 101,
    courseName: 'Yoga Intensivo',
    status: true,
    startDate: '2024-08-15',
    endDate: '2024-10-15',
    capacity: 20,
    trainer: { id: 1001, firstName: 'Elena Rojas' },
  },
  {
    id: 102,
    courseName: 'Spinning Matutino',
    status: true,
    startDate: '2024-09-01',
    endDate: '2024-12-01',
    capacity: 35,
    trainer: { id: 1002, firstName: 'Carlos Soto' },
  },
  {
    id: 103,
    courseName: 'Levantamiento Olímpico',
    status: false,
    startDate: '2024-10-20',
    endDate: '2024-11-20',
    capacity: 15,
    trainer: { id: 1003, firstName: 'Martín Pérez' },
  },
  {
    id: 104,
    courseName: 'Clase de Funcional',
    status: true,
    startDate: '2024-11-05',
    endDate: '2025-01-05',
    capacity: 40,
    trainer: { id: 1004, firstName: 'Laura Gómez' },
  },
  {
    id: 105,
    courseName: 'Pilates para Espalda',
    status: true,
    startDate: '2024-12-01',
    endDate: '2025-02-01',
    capacity: 25,
    trainer: { id: 1001, firstName: 'Elena Rojas' },
  },
  { id: 106, courseName: 'Zumba Fiesta', status: false, startDate: '2025-01-10', endDate: '2025-03-10', capacity: 50, trainer: null },
];

const ITEMS_PER_PAGE = 5;

const formatDate = dateString => {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' }).format(date);
};

const SortIcon = ({ direction }) => {
  const rotation = direction === 'asc' ? 'rotate-180' : '';
  return (
    <svg
      className={`inline w-3 h-3 ml-1 transition-transform ${rotation}`}
      fill="currentColor"
      viewBox="0 0 20 20"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        fillRule="evenodd"
        d="M14.707 10.293a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 111.414-1.414L9 12.586V5a1 1 0 012 0v7.586l2.293-2.293a1 1 0 011.414 0z"
        clipRule="evenodd"
      ></path>
    </svg>
  );
};

const PRIMARY_COLOR = '#00bfa5';
const PRIMARY_COLOR_HOVER = '#00a891';

export const Course = () => {
  const [loading, setLoading] = useState(false);
  const [paginationState, setPaginationState] = useState({
    activePage: 1,
    itemsPerPage: ITEMS_PER_PAGE,
    sort: 'id',
    order: 'asc',
  });

  const totalItems = mockCourseList.length;

  const handleSort = fieldName => {
    setPaginationState(prev => ({
      ...prev,
      order: prev.sort === fieldName && prev.order === 'asc' ? 'desc' : 'asc',
      sort: fieldName,
    }));
  };

  const sortedAndPagedList = useMemo(() => {
    const list = [...mockCourseList];

    list.sort((a, b) => {
      const aVal = a[paginationState.sort] ?? '';
      const bVal = b[paginationState.sort] ?? '';

      if (aVal < bVal) return paginationState.order === 'asc' ? -1 : 1;
      if (aVal > bVal) return paginationState.order === 'asc' ? 1 : -1;
      return 0;
    });

    const start = (paginationState.activePage - 1) * paginationState.itemsPerPage;
    const end = start + paginationState.itemsPerPage;
    return list.slice(start, end);
  }, [paginationState]);

  const handlePagination = page => {
    setPaginationState(prev => ({ ...prev, activePage: page }));
  };

  const handleSyncList = () => {
    setLoading(true);
    setTimeout(() => setLoading(false), 1000);
  };

  const getSortDirection = fieldName => {
    if (paginationState.sort !== fieldName) return 'none';
    return paginationState.order;
  };

  // -------------------------------------------------------------------
  // 🔥 FIX DEFINITIVO PARA TU ERROR: PROPS OPCIONALES
  // -------------------------------------------------------------------
  const ActionButton = ({
    onClick,
    color,
    icon,
    label,
    linkTo,
  }: {
    onClick?: () => void;
    color: string;
    icon: string;
    label: string;
    linkTo?: string;
  }) => {
    const base = `px-3 py-2 text-sm font-medium rounded-lg transition duration-150 ease-in-out whitespace-nowrap`;

    const theme =
      color === 'primary'
        ? 'bg-[#00bfa5] text-white hover:bg-[#00a891]'
        : color === 'outline'
          ? 'border-2 border-[#00bfa5] text-[#00bfa5] hover:bg-[#e0f2f1]'
          : 'bg-red-500 text-white hover:bg-red-600';

    const content = (
      <>
        <i className={`fa fa-${icon} mr-1`}></i>
        <span className="hidden md:inline">{label}</span>
      </>
    );

    if (linkTo)
      return (
        <a href={linkTo} className={`${base} ${theme}`}>
          {content}
        </a>
      );

    return (
      <button onClick={onClick} className={`${base} ${theme}`}>
        {content}
      </button>
    );
  };
  // -------------------------------------------------------------------

  return (
    <div style={{ ['--primary-turquesa' as any]: PRIMARY_COLOR }}>
      {/* HEADER */}
      <div className="flex justify-between items-center mb-6 p-4 bg-white shadow-lg rounded-xl border border-gray-200">
        <h2 id="course-heading" className="text-2xl font-semibold text-gray-800">
          Cursos Disponibles
        </h2>

        <div className="flex space-x-3">
          <ActionButton onClick={handleSyncList} color="outline" icon={loading ? 'spinner fa-spin' : 'sync'} label="Actualizar Lista" />
          <ActionButton linkTo="/course/new" color="primary" icon="plus" label="Crear nuevo Curso" />
        </div>
      </div>

      {/* TABLA */}
      <div className="bg-white shadow-xl rounded-xl overflow-hidden border border-gray-100">
        {loading && (
          <div className="p-6 text-center text-xl text-gray-500">
            <i className="fa fa-spinner fa-spin mr-2"></i> Cargando Cursos...
          </div>
        )}

        {!loading && sortedAndPagedList.length > 0 && (
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                {[
                  { key: 'id', label: 'ID', align: 'center' },
                  { key: 'courseName', label: 'Nombre del Curso', align: 'left' },
                  { key: 'capacity', label: 'Capacidad', align: 'center' },
                  { key: 'startDate', label: 'Inicio', align: 'center' },
                  { key: 'endDate', label: 'Fin', align: 'center' },
                  { key: 'trainer', label: 'Entrenador', align: 'center' },
                  { key: 'status', label: 'Estado', align: 'center' },
                ].map(({ key, label, align }) => (
                  <th
                    key={key}
                    className={`px-6 py-3 text-${align} text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100`}
                    onClick={() => handleSort(key)}
                  >
                    {label}
                    {getSortDirection(key) !== 'none' && <SortIcon direction={getSortDirection(key)} />}
                  </th>
                ))}

                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>

            <tbody className="bg-white divide-y divide-gray-200">
              {sortedAndPagedList.map(course => (
                <tr key={course.id} className="hover:bg-teal-50/50 transition duration-100">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 text-center">
                    <a href={`/course/${course.id}`} className="text-[#00bfa5] hover:text-[#00a891]">
                      {course.id}
                    </a>
                  </td>

                  <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                    <a href={`/course/${course.id}`} className="text-gray-800 hover:text-[#00bfa5]">
                      {course.courseName}
                    </a>
                  </td>

                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center">{course.capacity}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center">{formatDate(course.startDate)}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center">{formatDate(course.endDate)}</td>

                  <td className="px-6 py-4 whitespace-nowrap text-sm text-center">
                    {course.trainer ? (
                      <a href={`/user-data/${course.trainer.id}`} className="text-[#00bfa5] hover:text-[#00a891] font-medium">
                        {course.trainer.firstName}
                      </a>
                    ) : (
                      <span className="text-gray-400 italic">Sin asignar</span>
                    )}
                  </td>

                  <td className="px-6 py-4 whitespace-nowrap text-center">
                    <span
                      className={`px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        course.status ? 'bg-teal-100 text-[#00bfa5]' : 'bg-gray-100 text-gray-500'
                      }`}
                    >
                      {course.status ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>

                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                    <ActionButton linkTo={`/course/${course.id}`} color="outline" icon="eye" label="Ver" />
                    <ActionButton linkTo={`/course/${course.id}/edit`} color="primary" icon="pen" label="Editar" />
                    <ActionButton linkTo={`/course/${course.id}/delete`} color="danger" icon="trash" label="Eliminar" />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {!loading && sortedAndPagedList.length === 0 && (
          <div className="p-6 text-center text-xl text-gray-500">No se encontraron Cursos.</div>
        )}
      </div>

      {/* PAGINACIÓN */}
      {totalItems > 0 && (
        <div className="mt-6 flex flex-col md:flex-row justify-between items-center">
          <div className="text-sm text-gray-700 mb-2 md:mb-0">
            Mostrando {Math.min(totalItems, (paginationState.activePage - 1) * ITEMS_PER_PAGE + 1)} -{' '}
            {Math.min(totalItems, paginationState.activePage * ITEMS_PER_PAGE)} de {totalItems} elementos.
          </div>

          <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
            {Array.from({ length: Math.ceil(totalItems / ITEMS_PER_PAGE) }, (_, i) => i + 1).map(page => (
              <button
                key={page}
                onClick={() => handlePagination(page)}
                className={`relative inline-flex items-center px-4 py-2 border text-sm font-medium
                  ${
                    paginationState.activePage === page
                      ? 'z-10 bg-[#00bfa5] border-[#00bfa5] text-white'
                      : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                  }`}
              >
                {page}
              </button>
            ))}
          </nav>
        </div>
      )}
    </div>
  );
};

export default Course;
