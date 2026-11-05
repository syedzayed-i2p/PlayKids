import React from 'react';
import { Kid3DShape, KidShape } from '../types/game';

interface ShapeGraphicProps {
  shape: KidShape;
  color: string;
  sizePx?: number;
  isOutlineOnly?: boolean;
  isSilhouette?: boolean;
  rotationDegrees?: number;
  outlineColor?: string;
  strokeWidth?: number;
  className?: string;
}

export const ShapeGraphic: React.FC<ShapeGraphicProps> = ({
  shape,
  color,
  sizePx = 64,
  isOutlineOnly = false,
  isSilhouette = false,
  rotationDegrees = 0,
  outlineColor = '#94A3B8',
  strokeWidth = 6,
  className = '',
}) => {
  const activeFill = isSilhouette ? '#243B80' : color;
  const innerBorder =
    color.toUpperCase() === '#FAFAFA' || color.toUpperCase() === '#FFFFFF'
      ? '#64748B'
      : 'rgba(255,255,255,0.5)';

  const renderPolygon = (sides: number, startAngleDeg = -90, r = 38) => {
    const pts: string[] = [];
    const step = (2 * Math.PI) / sides;
    let angle = (startAngleDeg * Math.PI) / 180;
    for (let i = 0; i < sides; i++) {
      const x = 50 + r * Math.cos(angle);
      const y = 50 + r * Math.sin(angle);
      pts.push(`${x.toFixed(1)},${y.toFixed(1)}`);
      angle += step;
    }
    return pts.join(' ');
  };

  const renderStar = (points: number, outerR = 40, innerR = 18) => {
    const pts: string[] = [];
    const step = Math.PI / points;
    let angle = -Math.PI / 2;
    for (let i = 0; i < points; i++) {
      pts.push(
        `${(50 + outerR * Math.cos(angle)).toFixed(1)},${(50 + outerR * Math.sin(angle)).toFixed(1)}`
      );
      angle += step;
      pts.push(
        `${(50 + innerR * Math.cos(angle)).toFixed(1)},${(50 + innerR * Math.sin(angle)).toFixed(1)}`
      );
      angle += step;
    }
    return pts.join(' ');
  };

  const fillVal = isOutlineOnly ? 'none' : activeFill;
  const strokeVal = isOutlineOnly ? outlineColor : innerBorder;
  const sw = isOutlineOnly ? strokeWidth : 3.5;

  return (
    <svg
      width={sizePx}
      height={sizePx}
      viewBox="0 0 100 100"
      className={`inline-block shrink-0 ${className}`}
      style={{ transform: rotationDegrees ? `rotate(${rotationDegrees}deg)` : undefined }}
    >
      {shape.id === 'circle' && (
        <circle cx="50" cy="50" r="38" fill={fillVal} stroke={strokeVal} strokeWidth={sw} />
      )}

      {shape.id === 'triangle' && (
        <polygon
          points="50,12 88,86 12,86"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'square' && (
        <rect
          x="14"
          y="14"
          width="72"
          height="72"
          rx="4"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
        />
      )}

      {shape.id === 'star' && (
        <polygon
          points={renderStar(5, 42, 19)}
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'heart' && (
        <path
          d="M 50 28 C 50 12, 12 12, 12 38 C 12 62, 36 76, 50 88 C 64 76, 88 62, 88 38 C 88 12, 50 12, 50 28 Z"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'rectangle' && (
        <rect
          x="10"
          y="25"
          width="80"
          height="50"
          rx="4"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
        />
      )}

      {shape.id === 'oval' && (
        <ellipse cx="50" cy="50" rx="40" ry="26" fill={fillVal} stroke={strokeVal} strokeWidth={sw} />
      )}

      {shape.id === 'diamond' && (
        <polygon
          points="50,10 84,50 50,90 16,50"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'semicircle' && (
        <path
          d="M 12 65 A 38 38 0 0 1 88 65 Z"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'crescent' && (
        <path
          d="M 62 14 A 38 38 0 1 0 62 86 A 30 32 0 1 1 62 14 Z"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'pentagon' && (
        <polygon
          points={renderPolygon(5)}
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'hexagon' && (
        <polygon
          points={renderPolygon(6)}
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'octagon' && (
        <polygon
          points={renderPolygon(8, -67.5)}
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'trapezoid' && (
        <polygon
          points="28,22 72,22 88,78 12,78"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'parallelogram' && (
        <polygon
          points="28,22 90,22 72,78 10,78"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'cross' && (
        <polygon
          points="36,12 64,12 64,36 88,36 88,64 64,64 64,88 36,88 36,64 12,64 12,36 36,36"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'arrow' && (
        <polygon
          points="12,35 54,35 54,16 88,50 54,84 54,65 12,65"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'kite' && (
        <polygon
          points="50,10 82,38 50,90 18,38"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'heptagon' && (
        <polygon
          points={renderPolygon(7)}
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'decagon' && (
        <polygon
          points={renderPolygon(10)}
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'right_triangle' && (
        <polygon
          points="14,14 14,86 86,86"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'ring' && (
        <g>
          <path
            d="M 50 12 A 38 38 0 1 0 50 88 A 38 38 0 1 0 50 12 Z M 50 31 A 19 19 0 1 1 50 69 A 19 19 0 1 1 50 31 Z"
            fillRule="evenodd"
            fill={fillVal}
            stroke={strokeVal}
            strokeWidth={sw}
          />
        </g>
      )}

      {shape.id === 'four_point_star' && (
        <polygon
          points={renderStar(4, 42, 16)}
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'cloud_shape' && (
        <path
          d="M 28 74 L 74 74 A 14 14 0 0 0 76 46 A 18 18 0 0 0 42 36 A 16 16 0 0 0 18 52 A 14 14 0 0 0 28 74 Z"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}

      {shape.id === 'shield' && (
        <path
          d="M 50 12 L 84 26 C 84 58, 68 78, 50 88 C 32 78, 16 58, 16 26 Z"
          fill={fillVal}
          stroke={strokeVal}
          strokeWidth={sw}
          strokeLinejoin="round"
        />
      )}
    </svg>
  );
};

interface Shape3DGraphicProps {
  shape3D: Kid3DShape;
  sizePx?: number;
}

export const Shape3DGraphic: React.FC<Shape3DGraphicProps> = ({ shape3D, sizePx = 76 }) => {
  const color = shape3D.primaryColor;

  return (
    <svg width={sizePx} height={sizePx} viewBox="0 0 100 100" className="inline-block shrink-0">
      <defs>
        <radialGradient id={`grad_${shape3D.id}`} cx="35%" cy="35%" r="65%">
          <stop offset="0%" stopColor="#FFFFFF" />
          <stop offset="45%" stopColor={color} />
          <stop offset="100%" stopColor="#1E293B" />
        </radialGradient>
      </defs>

      {shape3D.id === 'sphere' && (
        <g>
          <circle cx="50" cy="50" r="38" fill={`url(#grad_${shape3D.id})`} />
          <ellipse
            cx="50"
            cy="50"
            rx="38"
            ry="12"
            fill="none"
            stroke="rgba(255,255,255,0.5)"
            strokeWidth="2.5"
            strokeDasharray="4 3"
          />
        </g>
      )}

      {shape3D.id === 'cube' && (
        <g stroke="#FFFFFF" strokeWidth="2.5" strokeLinejoin="round">
          <polygon points="50,14 84,32 50,50 16,32" fill={color} opacity="0.95" />
          <polygon points="16,32 50,50 50,86 16,68" fill={color} opacity="0.8" />
          <polygon points="50,50 84,32 84,68 50,86" fill={color} opacity="0.65" />
        </g>
      )}

      {shape3D.id === 'cuboid' && (
        <g stroke="#FFFFFF" strokeWidth="2.5" strokeLinejoin="round">
          <polygon points="14,38 34,22 88,22 68,38" fill={color} opacity="0.95" />
          <rect x="14" y="38" width="54" height="38" fill={color} opacity="0.82" />
          <polygon points="68,38 88,22 88,60 68,76" fill={color} opacity="0.65" />
        </g>
      )}

      {shape3D.id === 'cylinder' && (
        <g stroke="#FFFFFF" strokeWidth="2.5">
          <path
            d="M 22 26 L 22 72 A 28 10 0 0 0 78 72 L 78 26 Z"
            fill={color}
            opacity="0.82"
          />
          <ellipse cx="50" cy="26" rx="28" ry="10" fill={color} />
        </g>
      )}

      {shape3D.id === 'cone' && (
        <g stroke="#FFFFFF" strokeWidth="2.5" strokeLinejoin="round">
          <polygon points="50,14 82,74 18,74" fill={color} opacity="0.88" />
          <ellipse cx="50" cy="74" rx="32" ry="10" fill={color} />
        </g>
      )}

      {shape3D.id === 'pyramid' && (
        <g stroke="#FFFFFF" strokeWidth="2.5" strokeLinejoin="round">
          <polygon points="50,14 14,68 54,84" fill={color} opacity="0.92" />
          <polygon points="50,14 54,84 86,62" fill={color} opacity="0.68" />
        </g>
      )}

      {shape3D.id === 'tri_prism' && (
        <g stroke="#FFFFFF" strokeWidth="2.5" strokeLinejoin="round">
          <polygon points="36,22 68,14 88,62 56,74" fill={color} opacity="0.68" />
          <polygon points="36,22 14,74 56,74" fill={color} opacity="0.95" />
        </g>
      )}

      {shape3D.id === 'rect_prism' && (
        <g stroke="#FFFFFF" strokeWidth="2.5" strokeLinejoin="round">
          <polygon points="24,26 42,14 78,14 60,26" fill={color} opacity="0.95" />
          <rect x="24" y="26" width="36" height="58" fill={color} opacity="0.82" />
          <polygon points="60,26 78,14 78,72 60,84" fill={color} opacity="0.65" />
        </g>
      )}
    </svg>
  );
};
