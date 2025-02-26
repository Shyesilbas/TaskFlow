export const parseDate = (dateString) => {
    if (!dateString || typeof dateString !== 'string') {
        console.warn('Invalid or missing date string:', dateString);
        return new Date();
    }

    const isoDate = new Date(dateString);
    if (!isNaN(isoDate.getTime())) {
        return isoDate;
    }

    try {
        const [datePart, timePart] = dateString.split(' ');
        const [day, month, year] = datePart.split('-');
        const [hours, minutes] = timePart ? timePart.split(':') : ['00', '00'];
        const parsedDate = new Date(year, month - 1, day, hours || 0, minutes || 0);
        if (isNaN(parsedDate.getTime())) {
            throw new Error('Invalid date components');
        }
        return parsedDate;
    } catch (e) {
        console.error('Failed to parse date:', dateString, e);
        return new Date();
    }
};