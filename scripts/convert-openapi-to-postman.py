#!/usr/bin/env python3
"""
Convierte especificaciones OpenAPI 3.0 a Postman Collection v2.1
"""
import json
import os
import sys

def openapi_to_postman(openapi_file, output_file, base_url=None):
    with open(openapi_file, 'r', encoding='utf-8') as f:
        spec = json.load(f)

    # Info de la colección
    info_title = spec.get('info', {}).get('title', 'API Collection')
    info_desc = spec.get('info', {}).get('description', '')
    info_version = spec.get('info', {}).get('version', '1.0')

    # Servers
    servers = spec.get('servers', [])
    if not base_url and servers:
        base_url = servers[0].get('url', 'http://localhost')

    collection = {
        "info": {
            "name": info_title,
            "description": info_desc,
            "version": info_version,
            "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
        },
        "item": []
    }

    # Agrupar por tags
    tagged_endpoints = {}
    for path, methods in spec.get('paths', {}).items():
        for method, details in methods.items():
            if method in ('get', 'post', 'put', 'delete', 'patch'):
                tags = details.get('tags', ['General'])
                for tag in tags:
                    if tag not in tagged_endpoints:
                        tagged_endpoints[tag] = []
                    tagged_endpoints[tag].append({
                        'path': path,
                        'method': method.upper(),
                        'summary': details.get('summary', ''),
                        'description': details.get('description', ''),
                        'parameters': details.get('parameters', []),
                        'requestBody': details.get('requestBody', None),
                        'security': details.get('security', [])
                    })

    # Construir items
    for tag, endpoints in sorted(tagged_endpoints.items()):
        folder = {
            "name": tag,
            "item": []
        }

        for ep in endpoints:
            # Construir URL
            full_path = f"{base_url}{ep['path']}"
            
            # Construir path segments
            parsed_path = ep['path'].lstrip('/').split('/')
            
            # Headers
            headers = [
                {
                    "key": "Content-Type",
                    "value": "application/json",
                    "type": "text"
                }
            ]

            # Authorization
            auth_needed = any('bearerAuth' in sec or 'BearerAuth' in sec or 'JWT' in str(sec) 
                            for s in ep['security'] for sec in s)
            if not ep['security'] or auth_needed:
                # Check if any security scheme requires auth
                pass

            # Add Accept header  
            headers.append({
                "key": "Accept",
                "value": "*/*",
                "type": "text"
            })

            # URL object
            url_parts = []
            host = []
            port = None
            protocol = "http"

            if base_url:
                clean_url = base_url.replace('http://', '').replace('https://', '')
                if base_url.startswith('https'):
                    protocol = "https"
                if ':' in clean_url:
                    host_part, port_part = clean_url.split(':', 1)
                    host = [host_part]
                    port = port_part
                else:
                    host = [clean_url]

            # Parse path variables
            url_path = []
            variables = []
            for segment in parsed_path:
                if segment.startswith('{') and segment.endswith('}'):
                    var_name = segment[1:-1]
                    url_path.append(f":{var_name}")
                    variables.append({
                        "key": var_name,
                        "value": "",
                        "description": ""
                    })
                else:
                    url_path.append(segment)

            url_obj = {
                "raw": full_path,
                "protocol": protocol,
                "host": host,
                "path": url_path
            }
            if port:
                url_obj["port"] = port
            if variables:
                url_obj["variable"] = variables

            # Query parameters
            query_params = []
            for param in ep.get('parameters', []):
                if param.get('in') == 'query':
                    query_params.append({
                        "key": param['name'],
                        "value": "",
                        "description": param.get('description', ''),
                        "disabled": not param.get('required', False)
                    })
            if query_params:
                url_obj["query"] = query_params

            # Request body
            body = None
            if ep.get('requestBody'):
                content = ep['requestBody'].get('content', {})
                if 'application/json' in content:
                    schema = content['application/json'].get('schema', {})
                    # Try to get example
                    example = schema.get('example', None)
                    if not example and 'properties' in schema:
                        example = {}
                        for prop_name, prop_schema in schema['properties'].items():
                            example[prop_name] = prop_schema.get('example', '')
                    body = {
                        "mode": "raw",
                        "raw": json.dumps(example, indent=2, ensure_ascii=False) if example else "{}",
                        "options": {
                            "raw": {
                                "language": "json"
                            }
                        }
                    }

            request = {
                "method": ep['method'],
                "header": headers,
                "url": url_obj
            }
            if body:
                request["body"] = body

            item = {
                "name": ep['summary'] or f"{ep['method']} {ep['path']}",
                "request": request
            }

            folder["item"].append(item)

        collection["item"].append(folder)

    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(collection, f, indent=2, ensure_ascii=False)

    print(f"[OK] {output_file} creado ({len([i for f in collection['item'] for i in f['item']])} endpoints)")
    return collection


if __name__ == '__main__':
    base_dir = 'docs/postman'
    services = [
        ('api-gateway-openapi.json', 'api-gateway-postman.json', 'http://localhost:9090'),
        ('servicio-academico-openapi.json', 'servicio-academico-postman.json', 'http://localhost:8081'),
        ('servicio-asistencia-openapi.json', 'servicio-asistencia-postman.json', 'http://localhost:8082'),
        ('servicio-comunicaciones-openapi.json', 'servicio-comunicaciones-postman.json', 'http://localhost:8083'),
    ]

    collections = []
    for input_file, output_file, base_url in services:
        input_path = os.path.join(base_dir, input_file)
        output_path = os.path.join(base_dir, output_file)
        if os.path.exists(input_path):
            col = openapi_to_postman(input_path, output_path, base_url)
            collections.append(col)

    # Crear colección maestra
    master = {
        "info": {
            "name": "Colegio Bernardo O'Higgins - API Completa",
            "description": "Colección maestra que agrupa todos los microservicios del sistema.",
            "version": "1.0.0",
            "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
        },
        "item": [],
        "variable": [
            {
                "key": "base_url_gateway",
                "value": "http://localhost:9090",
                "type": "string"
            },
            {
                "key": "token",
                "value": "",
                "type": "string"
            }
        ],
        "auth": {
            "type": "bearer",
            "bearer": [
                {
                    "key": "token",
                    "value": "{{token}}",
                    "type": "string"
                }
            ]
        }
    }

    for col in collections:
        name = col['info']['name']
        master['item'].append({
            "name": name,
            "item": col['item']
        })

    master_path = os.path.join(base_dir, 'postman-collection-completa.json')
    with open(master_path, 'w', encoding='utf-8') as f:
        json.dump(master, f, indent=2, ensure_ascii=False)

    total = sum(len([i for f in col['item'] for i in f['item']]) for col in collections)
    print(f"\n[OK] Coleccion maestra creada: {master_path}")
    print(f"[OK] Total: {total} endpoints en {len(collections)} servicios")
